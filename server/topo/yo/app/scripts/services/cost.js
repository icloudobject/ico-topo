define(['angular'], function(angular) {
    'use strict';
    angular.module('icosvrWebApp.services.Cost', []).factory(
        'costService', function($http, $q,$log, apiBase) {
        return new CostService();
        // TenantService class
        function CostService () {
            var self = this;
            // current login user - to be $watch'ed from controllers
            self.loginUser = null;
            self.signupS3 =function(bucket) {
                console.info("Signup bucket "+bucket);
                return $q(function(resolve,reject) {
                    setTimeout(function() {
                        //var data={"status":0};
                        var data={"status":-2,"error":"the bucket " +bucket+" does not exist"};
                        var ret={data:data};
                        resolve(ret);
                    },1000);
                });
                //return $http.post(apiBase + '/billing/', {})
                //var id = cloudConfig._oid;
                //return $http.post(apiBase + '/settings/clouds/signupS3/' + id);  
            }
            self.getSummary = function() {   
                return $q(function(resolve,reject) {
                    var instanceTypes=["t1.micro","t2.medium","m3.medium","t3.large"];
                    setTimeout(function() {
                        var summaries=[];
                        for(var i=0;i<0;i++) {
                            var item={};
                            item.reserveType=(Math.random()>0.5)?"Reserved":"OnDemand";
                            item.type=instanceTypes[Math.floor(Math.random()*instanceTypes.length)];
                            item.cost=Math.floor((Math.random()*500));
                            item.time=item.cost/Math.floor(Math.random()*5+2);
                            summaries.push(item);
                        }

                        var data={"status":0,"data":summaries};
                        console.dir(data.data);
                        var ret={data:data};
                        //var data={"status":-2,"error":"bucket does NOT exist"};
                        resolve(ret);
                    },1000);
                });
            //	console.info("topo...");
                
               // return $http.get(apiBase + '/billing/', {});
                
                
            };

            var handleError = function(error) {
                $log.debug('An error occured: ' + JSON.stringify(error));
                throw error;
            };
        }

        

    });
});

