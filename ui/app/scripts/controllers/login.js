'use strict';

var $jq = jQuery.noConflict();

angular.module('timezonesuiApp')
  .controller('LoginCtrl', function ($scope, $location, $cookieStore, authorization, api, $window, $log) {

    $scope.login = function () {
        var credentials = {
            username: this.username,
            password: this.password
        };

        var success = function (data) {
            $log.debug('Login successful. Redirecting to /');
            var token = data.token;

            api.init(token);

            $cookieStore.put('tztoken', token);
            $location.path('/');
        };

        var error = function (data, status, headers, config) {
            $window.alert('login failed: ' + status);
            $cookieStore.remove('tztoken');
            api.init('');
        };

        authorization.login(credentials).success(success).error(error);
    };

    $scope.signup = function ($event) {
      //$event.preventDefault();

      // add validation here

        var credentials = {
            username: this.username,
            password: this.password
        };

        var success = function (data, status) {
            $log.debug('Sign up successful. Performing login.');
            $scope.login();
        };

        var error = function (data, status, headers, config) {
            $log.error('Sign up fail: ' + status);
            $window.alert('signup failed: ' + status);
            $cookieStore.remove('tztoken');
            api.init('');
        };

        authorization.create(credentials).success(success).error(error);
    };

  });

angular.module('timezonesuiApp').factory('authorization', function ($http) {
  return {
      login: function (credentials) {
        return $http({
          method: 'POST',
          url: '/api/users/authenticate',
          data: $jq.param({username: credentials.username, password: credentials.password}),
          headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        });
      },

      create: function (credentials) {
        return $http({
          method: 'POST',
          url: '/api/users',
          data: $jq.param({username: credentials.username, password: credentials.password}),
          headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        });
      }

  };
});
