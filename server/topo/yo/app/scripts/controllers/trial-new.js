define(['angular'], function(angular) {
    'use strict';



    /**
     * @ngdoc function
     * @name icosvrWebApp.controller:TrialNewCtrl
     * @description # TrialNewCtrl Controller of the icosvrWebApp
     */
    angular.module('icosvrWebApp.controllers.TrialNewCtrl', []).controller(
        'TrialNewCtrl', function($scope, $log, $window,$timeout, tenant, topoService,costService,wfResourcegridService) {

//common util for scroll
function scrollTo(elementId) {
  ///  $timeout(function() {
        $('body').animate({
            scrollTop: $("#"+elementId).offset().top
        }, 1000);
   // },200);
};

        $scope.cloudConfig = {
            cloudName: 'aws', // only allow aws for now
            credKey: '',
            credSecret: '',
            active: true
        };
        
        $scope.s3Config={
            "bucketName":'',
            "bucketFilePrefix":'/'
        };

        $scope.serverErrors = null;
        $scope.httpError = false;
        $scope.success = false;

        $scope.submitted = false;
        $scope.submitting = false;

        $scope.curStep=0;
        $scope.costGridOptions={
                columnDefs: [
                    {field:'reserveType', displayName:'ReserveType'}, 
                    {field:'type', displayName:'Compute Type'},
                    {field:'cost', displayName:'Cost'},
                    {field:'time', displayName:'Total Hours'}
                ]
           };
        function resetContext() {
            $scope.costGridOptions.data=[];
           $scope.tenant_data=[];

        };
        resetContext();
        $scope.startTrial = function() {

            // FIXME: Not used
            var topoData = null;

           // $scope.submitted = true;
           // if($scope.startTrialForm.$valid) {
             //   $scope.submitting = true;
                $.blockUI();
                $scope.signUpPromise=tenant.signupForTrial($scope.cloudConfig);
                $scope.signUpPromise.then(success, error);
            //}
            //else {
             //   $window.alert('Please fix input errors and try again.');
            //}

            function success(response) {
                var flag = response.data.success;
                resetContext();
                if(flag !== undefined && flag === false) {
                    $log.debug('Rejected: ' + JSON.stringify(response));
                    $scope.serverErrors = response.data.fieldErrors;
                    $.unblockUI();
                    return;
                }
                /*
                tenant.getUserInfo().then(function() {
                    $scope.success = true;
                    $scope.submitting = false;
                }, error);
                */

                $scope.initTopoPromise=topoService.initTopo();
                $scope.initTopoPromise.then(function(response) {
                    $scope.curStep=1;
                    $timeout(function() {
                        $scope.tenant_data = response.data;
                        $scope.topo_explore=topoService.explore;
                        scrollTo("topoPage");  
                    },100);
                    $.unblockUI();
                    //scrollTo("topoPage");
                    console.info("draw Data with " + JSON.stringify(response.data));
                },function FAIL() {
                    $.unblockUI();
                });
            }

            function error(httpError) {
                $log.debug('Error: ' + JSON.stringify(httpError));
                $.unblockUI();
                $scope.httpError = true;
            }

        }; //end of start trial

        $scope.signupS3=function() {
            $.blockUI();
            tenant.updateS3($scope.s3Config)
            .then(function SUCCESS(resp) {
                $scope.signupS3Promise=costService.signupS3($scope.s3Config.bucketName);
                $scope.signupS3Promise.then(function(resp) {

                    $scope.costGridOptions.data=[];
                    costService.getSummary().then(function(summaryData) {
                        $.unblockUI();
                        //console.info("SUMMARY="+JSON.stringify(summaryData));
                        $scope.curStep=2;
                        $timeout(function() {
                            $scope.costGridOptions.data =summaryData.data.data;
                            scrollTo("costPage");
                        },200);
                        
                    },function() {
                        $.unblockUI();
                    });// getSummary
                },function() {
                    $.unblockUI();
                });//signupS3Promise

            },function() {
                $.unblockUI();
            }); //update s3

        };//end of signupS3

        //optimize page
        $scope.openStep=function(step) {
            if(step.snapshot) {
                
                $scope.resourceGridData=step.snapshot;
            } else {
                $scope.resourceGridData=[];
            }
        };

        $scope.optimize=function() {
            $scope.curStep=3;
            
            $timeout(function() {
                scrollTo("optimizePage");
                wfResourcegridService.getSteps()
                .then(function(steps) {
                    $scope.resourceGridSteps=steps;
                    if(steps && steps.length>0 && steps[steps.length-1].snapshot) {
                        $scope.openStep(steps[steps.length-1]);
                    } else {
                        $scope.openStep(null);
                    }
                    //NO DIGEST HERE because setResourceGridData already trigger digest.
                });
            },200);
        }//end of optimize
        

    });
});
