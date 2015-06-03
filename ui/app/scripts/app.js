'use strict';

/**
 * @ngdoc overview
 * @name timezonesuiApp
 * @description
 * # timezonesuiApp
 *
 * Main module of the application.
 */
var app = angular
  .module('timezonesuiApp', [
    'ngAnimate',
    'ngCookies',
    'ngResource',
    'ngRoute',
    'ngSanitize',
    'ngTouch',
    'ui.sortable'
  ]);


app.config(function ($routeProvider, $httpProvider) {

    $httpProvider.interceptors.push('httpInterceptor');

    $routeProvider
      .when('/', {
        templateUrl: 'views/main.html',
        controller: 'MainCtrl'
      })
      .when('/about', {
        templateUrl: 'views/about.html',
        controller: 'AboutCtrl'
      })
      .when('/login', {
        templateUrl: 'views/login.html',
        controller: 'LoginCtrl'
      })
      .otherwise({
        redirectTo: '/'
      });

    // http://jjt.io/2013/11/16/angular-html5mode-using-yeoman-generator-angular/
    //$locationProvider.html5Mode(true);

  });

app.factory('httpInterceptor', function httpInterceptor ($q, $window, $location) {
 return {
   'responseError': function(response) {
      if (response.status === 401) {
          $location.url('/login');
      }
      return $q.reject(response);
    }
  };
});

app.factory('api', function ($http, $cookieStore) {
  return {
      init: function (token) {
          if(angular.isUndefined(token)) {
            token = $cookieStore.get('tztoken');
          }
          if(angular.isArray(token)) {
            token = token.length > 0 ? token[0] : '';
          }
          if(angular.isUndefined(token)) {
            token = '';
          }
          $http.defaults.headers.common['X-Access-Token'] = token;
      }
  };
});

app.run(function (api) {
  api.init();
});
