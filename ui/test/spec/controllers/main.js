'use strict';

describe('Controller: MainCtrl', function () {

  // load the controller's module
  beforeEach(module('timezonesuiApp'));

  var MainCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    MainCtrl = $controller('MainCtrl', {
      $scope: scope
    });
  }));

  it('should attach a list of timezones to the scope', function () {
    expect(scope.timezones.length).toBe(0);
  });

  it('should add items to the list', function () {
    scope.name = 'Test 1';
    scope.city = 'A City';
    scope.offset = 1;
    scope.addTimezone();
    expect(scope.timezones.length).toBe(1);
    expect(scope.timezones[0].name).toBe('Test 1');
  });

  it('should add then remove an item from the list', function () {
    scope.name = 'Test 1';
    scope.city = 'A City';
    scope.offset = 1;
    scope.addTimezone();
    expect(scope.timezones.length).toBe(1);
    scope.removeTimezone(0);
    expect(scope.timezones.length).toBe(0);
  });


});
