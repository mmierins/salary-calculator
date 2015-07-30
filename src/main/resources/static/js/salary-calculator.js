(function(){
    var app = angular.module("salary-calculator", []);

    app.directive("salaryCalculatorBe", function() {
        return {
            restrict: "E",
            templateUrl: "salary-calculator.html",
            controller: function($scope, CurrenciesService, SalaryCalculatorService, $timeout) {
                var promise = undefined;
                $scope.bruto = 360.0;
                $scope.persons = 0;
                $scope.currencies = [
                    { "name": "EUR", "rate": 1.00 }
                ];
                $scope.currency = $scope.currencies[0];
                $scope.tempData = {};

                CurrenciesService
                    .getCurrencies()
                    .success(function(data) {
                        data.forEach(function(item) {
                            $scope.currencies.push(item);
                        });
                        $scope.currencies.sort(function(a, b) {
                            if (a.name > b.name) {
                                return 1;
                            }
                            if (a.name < b.name) {
                                return -1;
                            }
                            return 0;
                        });
                    })
                    .error(function(data) {
                        alert("Could not load currencies!");
                    });

                $scope.update = function(timeout) {
                    if (promise) {
                        $timeout.cancel(promise);
                    }

                    promise = $timeout(function () {
                        SalaryCalculatorService
                            .calculate($scope.bruto, $scope.persons, $scope.currency.name)
                            .success(function (data) {
                                $scope.tempData = data;
                                $scope.currency = $scope.currency;
                            })
                            .error(function (data) {
                                alert("Could not access salary conversion service!");
                            });
                    }, timeout);
                };

                $scope.update();

                $scope.getNeto = function() {
                    return $scope.tempData.neto;
                };

                $scope.getEmployerFullPayment = function() {
                    return $scope.tempData.employerFullPayment;
                };
            }
        };
    });

    app.directive("salaryCalculatorFe", function() {
       return {
           restrict: "E",
           templateUrl : "salary-calculator.html",
           controller : function($scope, CurrenciesService) {
                /* all absolute values given in EUR */
                var NON_TAXABLE_MIN = 75.0;
                var ENTREPRENEURSHIP_RISK = 0.36;
                var PERSON_DEDUCTION = 165.00;

                var SOCIAL_TAX_EMPLOYEE_PERC = 10.50;
                var SOCIAL_TAX_EMPLOYER_PERC = 23.59;
                var IIN_TAX_PERC = 23.0;

                $scope.bruto = 360.0;
                $scope.persons = 0;
                $scope.currencies = [
                   { "name": "EUR", "rate": 1.00}
                ];
                $scope.currency = $scope.currencies[0];

                CurrenciesService
                   .getCurrencies()
                   .success(function(data) {
                       data.forEach(function(item) {
                           $scope.currencies.push(item);
                       });
                       $scope.currencies.sort(function(a, b) {
                           if (a.name > b.name) {
                               return 1;
                           }
                           if (a.name < b.name) {
                               return -1;
                           }
                           return 0;
                       });
                   })
                   .error(function(data) {
                       alert("Could not load currencies!");
                   });

                function calcPerc(sum, perc) {
                   return sum * (perc/100);
                };

                function calcSocialTaxForEmployee(bruto) {
                   return calcPerc(bruto, SOCIAL_TAX_EMPLOYEE_PERC);
                };

                function calcSocialTaxForEmployer(bruto) {
                   return calcPerc(bruto, SOCIAL_TAX_EMPLOYER_PERC);
                };

                function calcIIN(bruto) {
                   var notPerc = bruto - calcSocialTaxForEmployee(bruto) -
                       NON_TAXABLE_MIN  - ($scope.persons * PERSON_DEDUCTION);
                   return calcPerc(notPerc, IIN_TAX_PERC);
                };

                function calcNetoSalary(bruto) {
                   return bruto - calcSocialTaxForEmployee(bruto) - calcIIN(bruto);
                };

                function convertCurrency(euros) {
                   return euros * $scope.currency.rate;
                };

                $scope.getNeto = function() {
                   if ($scope.bruto > 0) {
                       var euros = calcNetoSalary($scope.bruto);
                       return convertCurrency(euros);
                   } else {
                       return 0;
                   }
                };

                $scope.getEmployerFullPayment = function() {
                   if ($scope.bruto > 0) {
                       var euros = $scope.bruto +
                                   calcSocialTaxForEmployer($scope.bruto) +
                                   ENTREPRENEURSHIP_RISK;
                       return convertCurrency(euros);
                   } else {
                       return 0;
                   }
                };
           }
       };
    });

})();