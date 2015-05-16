define(['angular'], function(angular) {
    'use strict';

    /**
     * @ngdoc service
     * @name icosvrWebApp.Tenant
     * @description # Tenant Service in the icosvrWebApp.
     */
    angular.module('icosvrWebApp.services.Tenant', []).factory(
        'tenant', function($http, $log,$q, apiBase) {

        // TenantService class
        function TenantService () {

            var self = this;

            // current login user - to be $watch'ed from controllers
            self.loginUser = null;

            self.getUserInfo = function() {
                return $http.get(apiBase + '/userinfo')
                    .then(function(response) {
                        self.loginUser = response.data;
                        return response;
                    });
            };
            self.updateS3=function(s3Config) {
                    console.info("Update s3" +JSON.stringify(s3Config));
                 return $q(function(resolve,reject) {
                    setTimeout(function() {
                        var data={data:{success:true}};
                        resolve(data);
                    },1000);
                });
                
                //return $http.post(apiBase + '/signup/updateS3', s3Config);
            };
            self.signupForTrial = function(cloudConfig) {
                console.info("signUp");
                return $q(function(resolve,reject) {
                    setTimeout(function() {
                        var data={data:{success:true}};
                        resolve(data);
                    },1000);
                });
               // return $http.post(apiBase + '/signup/trial', cloudConfig);

            };

            self.performLogin = function(loginCreds) {
                return $http.post(apiBase + '/login', loginCreds);
            };

            self.getAgentInfo = function() {
                return $http.get(apiBase + '/settings/agent');
            };

            self.changePassword = function(passCombo) {
                return $http.post(apiBase + '/settings/password', passCombo);
            };

            self.updateProfile = function(profile) {
                return $http.post(apiBase + '/settings/profile', profile);
            };

            self.listClouds = function() {
                return $http.get(apiBase + '/settings/clouds');
            };

            self.addCloud = function(cloudConfig) {
                return $http.post(apiBase + '/settings/clouds/add', cloudConfig);
            };

            self.deleteCloud = function(cloudConfig) {
                var id = cloudConfig._oid;
                return $http.delete(apiBase + '/settings/clouds/delete/' + id);
            };

            self.switchCloud = function(cloudConfig) {
                var id = cloudConfig._oid;
                return $http.post(apiBase + '/settings/clouds/swich' + id);
            };

            
        }

        return new TenantService();

    });
});

