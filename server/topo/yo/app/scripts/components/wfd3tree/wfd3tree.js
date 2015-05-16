define(['angular','components/wfd3tree/wfd3treeDirective',
	'components/wfd3tree/wfd3treeService'],
function(angular,wfd3treeDirective) {
'use strict';

/****BEGIN DEFINITION*****/
angular.module('wfd3tree',[
	'wfd3tree.wfd3treeDirective',
	'wfd3tree.wfd3treeService',
])
.controller('wfd3treeCtrl',['$scope','wfd3treeService',Wfd3treeCtrl]);

function Wfd3treeCtrl($scope,wfd3treeService) {
	$scope.loadTree=function() {
		if($scope.loadmask) {
			$scope.loadmask();
		}
		wfd3treeService.getTreeData()
		.then(function(treeData) {
			$scope.wfd3tree_data=treeData;
			
			if($scope.unloadmask) $scope.unloadmask();
		},function(error) {
			if($scope.unloadmask) $scope.unloadmask();
		});
	}
};


});