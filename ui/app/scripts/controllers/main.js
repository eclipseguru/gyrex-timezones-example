'use strict';

/**
 * @ngdoc function
 * @name timezonesuiApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the timezonesuiApp
 */
angular.module('timezonesuiApp')
  .controller('MainCtrl', function ($scope, timezones, $timeout, $log) {

    $scope.timezones = [ ];

    $scope.addTimezone = function () {
      var timezone = {
        name: $scope.name,
        city: $scope.city,
        offset: $scope.offset,
      };

      $scope.timezones.push(timezone);
      $scope.name = '';
      $scope.city = '';
      $scope.offset = '';

      $scope.saveTimezones();
    };

    $scope.removeTimezone = function (index) {
      $scope.timezones.splice(index, 1);

      $scope.saveTimezones();
    };

    // timeout to display the current time of all entries
    var refreshTimes = function() {
        for (var i = 0; i < $scope.timezones.length; i++) {
          $scope.timezones[i].currentTime = moment().utc().utcOffset($scope.timezones[i].offset).format('HH:mm:ss ddd., MMM. Do');
        }
        $timeout(refreshTimes, 1000);
    };
    $timeout(refreshTimes, 1000);

    // initial load when opening page
    timezones.load(function(data) {
      if(angular.isArray(data)) {
        $scope.timezones = data;
        refreshTimes();
      }
    });

    // capture form state for auto save
    $scope.displayform = {
      state: {}
    };

    // auto save callback
    $scope.saveTimezones = function () {
      timezones.save($scope.timezones);
    };

    // cllback to save on sorting
    $scope.sortCallback = {
      stop: function(e, ui) {
        //$scope.saveTimezones();
        $scope.displayform.state.$dirty = true;
      }
    };


  });


angular.module('timezonesuiApp').factory('timezones', function ($http, $log) {
  return {
      load: function (callback) {
          $log.debug('Fetching timezones from server.');
          return $http.get('/api/timezones').
            success(function(data) {
              callback(data);
          });
      },

      save: function (timezones, callback) {
          $log.debug('Saving timezones to server.');
          return $http.put('/api/timezones', timezones).
            success(function(data) {
              $log.info('Timezones saved successfully to server.');
          });
      }
  };
});


// a directive for auto-save behavior
// (http://stackoverflow.com/questions/21135302/angularjs-autosave-form-is-it-the-right-way)
angular.module('timezonesuiApp').directive('autoSaveForm', function($timeout) {

  return {
    require: ['^form'],
    link: function($scope, $element, $attrs, $ctrls, $log) {

      var $formCtrl = $ctrls[0];
      var savePromise = null;
      var expression = $attrs.autoSaveForm || 'true';

      $scope.$watch(function() {

        if($formCtrl.$valid && $formCtrl.$dirty) {

          if(savePromise) {
            $timeout.cancel(savePromise);
          }

          savePromise = $timeout(function() {

            savePromise = null;

            // Still valid?

            if($formCtrl.$valid) {

              if($scope.$eval(expression) !== false) {
                $formCtrl.$setPristine();
              }

            }

          }, 500);
        }

      });
    }
  };

});
