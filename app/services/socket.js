angular.module(PKG.name+'.services')

  .constant('MYSOCKET_EVENT', {
    message: 'mysocket-message',
    closed: 'mysocket-closed',
    reconnected: 'mysocket-reconnected'
  })

  /*
    MyDataSource // usage in a controler:

    var dataSrc = new MyDataSource($scope);
    dataSrc.poll({method:'GET', url: '/v2/foo/bar'}, function(result) {
      $scope.foo = result;
    });

   */
  .factory('MyDataSource', function ($state, mySocket, MYSOCKET_EVENT) {

    var instances = {}; // keyed by scopeid

    function DataSource (scope) {
      var id = scope.$id,
          self = this;

      if(instances[id]) {
        throw new Error('multiple DataSource for scope', id);
      }
      instances[id] = self;

      this.bindings = [];

      scope.$on(MYSOCKET_EVENT.message, function (event, data) {
        if(data.warning) { return; }

        angular.forEach(self.bindings, function (b) {
          if(angular.equals(b.resource, data.resource)) {
            scope.$apply(b.callback.bind(self, data.response));
          }
        });
      });

      scope.$on(MYSOCKET_EVENT.reconnected, function (event, data) {
        console.log('[DataSource] reconnected, reloading...');

        // https://github.com/angular-ui/ui-router/issues/582
        $state.transitionTo($state.current, $state.$current.params,
          { reload: true, inherit: true, notify: true }
        );
      });

      scope.$on('$destroy', function () {
        delete instances[id];
      });

      this.scope = scope;
    }


    DataSource.prototype.poll = function (resource, cb) {

      this.bindings.push({
        resource: resource,
        callback: cb
      });

      this.scope.$on('$destroy', function () {
        mySocket.send({
          action: 'poll-stop',
          resource: resource
        });
      });

      mySocket.send({
        action: 'poll-start',
        resource: resource
      });
    };


    DataSource.prototype.fetch = function (resource, cb) {
      var once = false;

      this.bindings.push({
        resource: resource,
        callback: function() {
          if(!once) {
            once = true;
            cb.apply(this, arguments);
          }
        }
      });

      mySocket.send({
        action: 'fetch',
        resource: resource
      });
    };


    return DataSource;
  })




  .provider('mySocket', function () {

    this.prefix = '/_sock';

    this.$get = function (MYSOCKET_EVENT, myAuth, $rootScope) {

      var self = this,
          socket = null,
          buffer = [];

      function init (attempt) {
        console.log('[mySocket] init');

        attempt = attempt || 1;
        socket = new window.SockJS(self.prefix);

        socket.onmessage = function (event) {
          try {
            var data = JSON.parse(event.data);
            console.log('[mySocket] ←', data.statusCode);
            $rootScope.$broadcast(MYSOCKET_EVENT.message, data);
          }
          catch(e) {
            console.error(e);
          }
        };

        socket.onopen = function (event) {

          if(attempt>1) {
            $rootScope.$broadcast(MYSOCKET_EVENT.reconnected, event);
            attempt = 1;
          }

          console.info('[mySocket] opened');
          angular.forEach(buffer, send);
          buffer = [];
        };

        socket.onclose = function (event) {
          console.error(event.reason);

          if(attempt<2) {
            $rootScope.$broadcast(MYSOCKET_EVENT.closed, event);
          }

          // reconnect with exponential backoff
          var d = Math.max(500, Math.round(
            (Math.random() + 1) * 500 * Math.pow(2, attempt)
          ));
          console.log('[mySocket] will try again in ',d+'ms');
          setTimeout(function () {
            init(attempt+1);
          }, d);
        };

      }

      function send(obj) {
        if(!socket.readyState) {
          buffer.push(obj);
          return false;
        }

        var msg = angular.extend({

              user: myAuth.currentUser

            }, obj),
            r = obj.resource;

        if(r) {
          // we only support json content-type,
          // and expect json as response
          msg.resource.json = true;

          // parse the _cdap key, prefix with the CDAP protocol/host
          // @TODO get prefix from config
          if(r._cdap) {
            var p = r._cdap.split(' '),
                path = p.pop();
            msg.resource.method = p.length ? p[0] : 'GET';
            msg.resource.url = 'http://localhost:10000/v2' + path;
            delete msg.resource._cdap;
          }
        }

        console.log('[mySocket] →', msg);
        socket.send(JSON.stringify(msg));
        return true;
      }

      init();

      return {
        init: init,
        send: send,
        close: function () {
          return socket.close.apply(socket, arguments);
        }
      };
    };

  })

  ;