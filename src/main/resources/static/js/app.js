(function() {
    var app = angular.module("calc", ["rest-services", "salary-calculator"]);

    /*
     * apply if you want to restrict input values to digits only
     */
    app.directive('onlyDigits', function () {
        return {
            restrict: 'A',
            require: '?ngModel',
            scope: {
                model: '=ngModel',
            },
            link: function (scope, element, attrs, ngModel) {
                if (!ngModel) return;
                ngModel.$parsers.unshift(function (inputValue) {
                    var digits = inputValue.split('').filter(function (s) {
                        return (!isNaN(s) && s != ' ');
                    }).join('');
                    ngModel.$viewValue = digits;
                    ngModel.$render();
                    return digits;
                });
            }
        }
    });

    /*
     * apply if you want to do 2 way binding between
     * text field and numeric model field
     */
    app.directive('numericBinding', function () {
        return {
            restrict: 'A',
            require: 'ngModel',
            scope: {
                model: '=ngModel',
            },
            link: function (scope, element, attrs, ngModelCtrl) {
                if (scope.model && typeof scope.model == 'string') {
                    scope.model = parseFloat(scope.model);
                }
                scope.$watch('model', function(val, old) {
                    if (typeof val == 'string') {
                        scope.model = parseFloat(val);
                    }
                });
            }
        };
    });
})();
