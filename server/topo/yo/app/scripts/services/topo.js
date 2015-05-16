define(['angular'], function(angular) {
    'use strict';
    angular.module('icosvrWebApp.services.Topo', []).factory(
        'topoService', function($http, $q,$log, apiBase) {
        return new TopoService();
        // TenantService class
        function TopoService () {
            var self = this;
            // current login user - to be $watch'ed from controllers
            self.loginUser = null;
            
            self.initTopo = function() {
                
                return $q(function(resolve,reject) {
                    setTimeout(function() {
                        var data={"type":"Cloud","id":"ROOT","name":"ROOT","children":[{"type":"Region","id":"us-west-2","name":"us-west-2","children":[{"type":"AvailabilityZone","id":"us-west-2a","name":"us-west-2a","children":[{"type":"Instance","id":"i-2e32fb24","name":"i-2e32fb24","children":[]}]}]}]};
                        var ret={data:data};
                        resolve(ret);
                    },1000);
                });
            	//console.info("topo...");
                
               //return $http.post(apiBase + '/topo/', {});
                
                
            };

            self.explore = function(node) {
                return $http.post(apiBase + '/topo_next/'+node.type+"/"+node.id, {})
                /*.then(function(response) {
                    console.info("explore return");
                    console.dir(response);
                 })
                .catch(function(error) {
                    handleError(error);
                });*/
            };

            var handleError = function(error) {
                $log.debug('An error occured: ' + JSON.stringify(error));
                throw error;
            };
        }

        

    });
});

