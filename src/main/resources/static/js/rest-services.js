var services = angular.module("rest-services", []);

services.factory("CurrenciesService", function($http) {
    return {
        getCurrencies : function() {
            return $http.get("currencies");
        }
    };
});

services.factory("SalaryCalculatorService", function($http) {
    return {
        calculate : function(bruto, persons, currency) {
            return $http({
                url: "calculator",
                method: "GET",
                params: {
                    bruto: bruto,
                    persons: persons,
                    currency: currency
                }
            });
        }
    };
});
