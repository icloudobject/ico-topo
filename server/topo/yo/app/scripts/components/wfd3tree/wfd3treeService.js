define(['angular'], function(angular) {
'use strict';

angular.module('wfd3tree.wfd3treeService',[])
	.factory('wfd3treeService',['$http','$q','$timeout',Wfd3treeService]);

function Wfd3treeService($http,$q,$timeout) {
	function getTreeData() {
		return $q(function(resolve,reject) {
			$timeout(function() {
				var id=1;
				var root={"id":id,"type":"Tenant","name":"demo","color":"black"};
				function genNext(arr,type,color,len) {
					var ret=[];
					$.each(arr,function(ind,val) {
						for(var i=0;i<len;i++) {
							var ob={"id":++id,"type":type,"name":type+" "+id,"color":color};
							if(!val.children) val.children=[];
							val.children.push(ob);
							ret.push(ob);
						}
					})
					return ret;
				} 
				var arr=genNext([root],"Cloud","red",2);
				arr=genNext(arr,"Region","blue",3);
				arr=genNext(arr,"Compute","green",10);

				//var data={"id":"1","type":"Tenant","name":"demo","color":"red","children":[{"id":"2","type":"Cloud","name":"AWS","color":"green","children":[{"id":"3","type":"AZ","name":"dc_slc","color":"orange","children":[{"id":"demo01.dev.aws.com","type":"Compute","name":"demo01.dev.aws.com","color":"grey","children":[{"id":"cron","type":"Process","name":"cron","color":"black","children":[]},{"id":"Tomcat run","type":"Process","name":"Tomcat run","color":"black","children":[]},{"id":"mysql.sh","type":"Process","name":"mysql.sh","color":"black","children":[]},{"id":"mongodb","type":"Process","name":"mongodb","color":"black","children":[]},{"id":"oracle.sh","type":"Process","name":"oracle.sh","color":"black","children":[]},{"id":"game1","type":"Process","name":"game1","color":"black","children":[]},{"id":"java","type":"Process","name":"java","color":"black","children":[]}]},{"id":"demo04.dev.aws.com","type":"Compute","name":"demo04.dev.aws.com","color":"grey","children":[]}]}]}]};
				resolve(root);
			},2000);
		});
	};
	function getDetail(resourceId) {
		return $q(function(resolve,reject) {
			$timeout(function() {
				var ret={"name":"demo","type":"tenant"};
				resolve(ret);
			},100);
		});
	};
	function exploreNode(node) {
		return $q(function(resolve, reject) {
			$timeout(function() {
				var ret=[];
				for(var i=0;i<2;i++) {
					var id=Math.floor(Math.random()*200);
					ret.push({"id":id,"type":"Compute","name":"instance "+id,"color":"green"});
				}
				resolve(ret);
			},100);
		});
	};
	return {
		getTreeData:getTreeData,
		getDetail:getDetail,
		exploreNode:exploreNode
	};
}


});