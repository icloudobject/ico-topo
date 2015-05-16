var zoomL=null;

define(['angular','d3','tooltipster'], function (angular,d3,tooltipster) {
  'use strict';

angular.module('wfd3tree.wfd3treeDirective',[])
.directive('wfd3treeDirective',['$window','wfd3treeService',Wfd3treeDirective])
;

function Wfd3treeDirective($window,wfd3treeService) {
	var $w = angular.element($window);
  	return {
      restrict:'EA',
      transclude:false,
      scope:{
      	wfData:'=',
      	wfExplore:'='
      },
      link: function($scope,elm,attrs) {
      	$scope.$watch('wfData',setTreeData,true);
      	function setTreeData(newVal,oldVal) {
      		if(!newVal) return;
      		
      		var tree=new wf_tree({
      			exploreNode:$scope.wfExplore,
      			detailCallback:wfd3treeService.getDetail,
		       	elm:elm,
		       	focusId:4,
		        linkLength:150,
		        width:$w.width()*0.9,
		        height:$w.height()*0.9,
		        data:angular.copy(newVal)
		    });
      	}
      }
  };
};
//end of wfd3treeDirective

/****definition of d3 legend***/
d3.legend = function(g) {
  g.each(function() {
    var g= d3.select(this),
        items = {},
        svg = d3.select(g.property("nearestViewportElement")),
        legendPadding = g.attr("data-style-padding") || 5,
        lb = g.selectAll(".legend-box").data([true]),
        li = g.selectAll(".legend-items").data([true])

    lb.enter().append("rect").classed("legend-box",true)
    li.enter().append("g").classed("legend-items",true)

    svg.selectAll("[data-legend]").each(function() {
        var self = d3.select(this)
        items[self.attr("data-legend")] = {
          pos : self.attr("data-legend-pos") || this.getBBox().y,
          color : self.attr("data-legend-color") != undefined ? self.attr("data-legend-color") : self.style("fill") != 'none' ? self.style("fill") : self.style("stroke") 
        }
      })

    items = d3.entries(items).sort(function(a,b) { return a.value.pos-b.value.pos})

    
    li.selectAll("text")
        .data(items,function(d) { return d.key})
        .call(function(d) { d.enter().append("text")})
        .call(function(d) { d.exit().remove()})
        .attr("y",function(d,i) { return i+"em"})
        .attr("x","1em")
        .text(function(d) { ;return d.key})
    
    li.selectAll("circle")
        .data(items,function(d) { return d.key})
        .call(function(d) { d.enter().append("circle")})
        .call(function(d) { d.exit().remove()})
        .attr("cy",function(d,i) { return i-0.25+"em"})
        .attr("cx",0)
        .attr("r","0.4em")
        .style("fill",function(d) { console.log(d.value.color);return d.value.color})  
    
    // Reposition and resize the box
    var lbbox = li[0][0].getBBox();
     if(lbbox.height==0 || lbbox.width==0) {
    	lbbox.height=items.length*10+10;
    	lbbox.width=120;
    	lbbox.x=-10;
    	lbbox.y=-16;
    }

    lb.attr("x",(lbbox.x-legendPadding))
        .attr("y",(lbbox.y-legendPadding))
        .attr("height",(lbbox.height+2*legendPadding))
        .attr("width",(lbbox.width+2*legendPadding))
        .attr("fill","white")
        .attr("stroke","black");

  })
  return g;
};
/*end of d3 legend***/

/**definition of wftree***/
function wf_tree(options) {		
	//public variable:
	this.store={};
	var nodeMap={};// broad transverse
	var circle_size=4;
	// Calculate total nodes, max label length
	var totalNodes = 0;
	var maxLabelLength = 0;
	var hideTip=false;
	// variables for drag/drop
	var selectedNode = null;
	var draggingNode = null;
	var treeData=options.data;
	// panning variables
	var panSpeed = 200;
	var panBoundary = 20; // Within 20px from edges will pan when dragging.
	// Misc. variables
	var i = 0;
	var duration = 500;
	var root;
	//if not set default focus, then focus on root
	var focusId=options.focusId || treeData.id;
	var focusNode=null;

	// size of the diagram
	var viewerWidth =options.width;
	var viewerHeight =options.height;
	var elm=options.elm[0];
	
	//setup tree
	var tree = d3.layout.tree().size([viewerHeight,viewerWidth]);
	// define a d3 diagonal projection for use by the node paths later on.
	var diagonal = d3.svg.diagonal().projection(function(d) {return [d.y, d.x];});

	// A recursive helper function for performing some setup by walking through all nodes with broadcast
	function visit(maxLv,parent, visitFn) {
		if (!parent) return;
		var q1=[],q2=[];
		var lv=0;
		q1.push(parent);
		while(q1.length>0) {
			var node=q1.splice(0,1)[0];
			if(!node) break;
			if(!visitFn(lv,node)) return;
			var children = node.children || node._children; 
			if (children && children.length>0) {      
				for (var i = 0; i < children.length; i++) {
					children[i]._parent=node;
					q2.push(children[i]);
				}
			}

			if(maxLv>=0){
				if(lv>=maxLv) return;
			}
			if(q1.length==0) {
				q1=q2;q2=[];
				lv++;
			}
		}

	}


	function zoom() {
		svgGroup.attr("transform", "translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
	}

	function activeToolTip() {
		var config={
				content : $('<h3 class="popover-title"><b>All Properties</b></h3><div class="popover-content"><i class="icon-spinner icon-spin icon-large"></i></div>'),
				functionBefore : function(origin, continueTooltip) {
					console.info(origin.data("name"));
					continueTooltip();
					if(origin.data("tip")=='ok') return;
					options.detailCallback(origin.data("name"))
					.then(function(response) {
							var sDetail=[];
							sDetail.push('<h3 class="popover-title"><b>All Properties</b></h3>');
							sDetail.push('<div class="popover-content"><table class="table table-hover table-condensed"><tbody>');
							$.each(response,function(key,value){
								sDetail.push('<tr class="tooltiptr"><td><b>'+key+'</b></td><td class="value">' + value + '</td></tr>');
							});			

							sDetail.push('</tbody></table></div>');

							// update our tooltip content with our returned data
							// and cache it
							origin.tooltipster('update', $(sDetail.join("\r\n"))).attr('data-tip', 'ok');

					});

				},
				functionAfter : function(origin) {
					origin.attr("class", "nodeCircle tooltipster");
				},
				theme : ".tooltipster-shadow",
				maxWidth : 500,
				interactive : true,
				interactiveTolerance : 350,
				updateAnimation : false,
				trigger : "hover",
				delay:"500",
				position : "top",
				offsetX : "0",
				offsetY : "0"

		};

		$("circle:not(.tooltipster)",elm).tooltipster(config);
	};
	//END tooltip


	// define the zoomListener which calls the zoom function on the "zoom" event constrained within the scaleExtents
	var zoomListener = d3.behavior.zoom().scaleExtent([1,1]).on("zoom", zoom);


	// define the baseSvg, attaching a class for styling and the zoomListener
	var baseSvg = null;
	if(d3.select("svg",elm).size()>0) {
		d3.select("svg",elm).remove();
    }
	baseSvg=d3.select(elm).append("svg").attr("width", viewerWidth)
	.attr("height", viewerHeight)
	.attr("class", "overlay")
	.style("background-color","white")
	.call(zoomListener);


	// Helper functions for collapsing and expanding nodes.

	function collapse(d) {
		if (d.children) {
			d._children = d.children;
			d._children.forEach(collapse);
			d.children = null;
		}
	}

	function expand(d) {
		if (d._children) {
			d.children = d._children;
			d.children.forEach(expand);
			d._children = null;
		}
	}



	// Function to center node when clicked/dropped so node doesn't get lost when collapsing/moving with large amount of children.
	function centerNode(source,originX,originY) {
		var scale = zoomListener.scale();
		var curViewPos=zoomListener.translate();
		zoomL=zoomListener;
		//console.info("curPos="+curViewPos[0]+","+curViewPos[1]);
		//console.info("source="+source.x+","+source.y);
		if(!originX) originX=viewerWidth*0.4;
		if(!originY) originY=viewerHeight*0.4;
		
		var x=originX-source.y*scale;
		var y=originY-source.x*scale;
		d3.select('g').transition()
		.duration(duration)
		.attr("transform", "translate(" + x + "," + y + ")");
		//.attr("transform", "translate(" + x + "," + y + ")scale(" + scale + ")");
		//console.info("move to "+x+","+y+"="+(viewerWidth*0.4)+"-"+source.x+"*"+scale);
		//zoomListener.scale(scale);
		zoomListener.translate([x, y]);
	}

	// Toggle children function

	function toggleChildren(d) {
		if (d.children) {
			d._children = d.children;
			d.children = null;
		} else if (d._children) {
			d.children = d._children;
			d._children = null;
		}

		return d;
	}

	// Toggle children on click.

	function click(d) {
		var originViewPos=zoomListener.translate();
		var originY=originViewPos[1]+d.x;
		var originX=originViewPos[0]+d.y;
		//explore node
		
		if((!d.children || d.children.length==0) && (!d._children || d._children.length==0) && !d.explored && options.exploreNode) {
			options.exploreNode(d).then(function(array){
				d.explored=true;
				if(array && array.data) array=array.data;
				if(array) {
					var newChildren=[];
					$.each(array,function(arr_ind,arr_val) {
						if(nodeMap[arr_val.id]) {
							
						} else {
							newChildren.push(arr_val);
							nodeMap[arr_val.id]=arr_val;
						}
					});
					d.children=newChildren;
				}
				update(d);
				centerNode(d,originX,originY);
			});
		} else {
			//console.info(originX+"("+d.y+","+originViewPos[0]+"),"+originY+"("+d.x+","+originViewPos[1]+")");
			d = toggleChildren(d);
			update(d);
			//activeToolTip();
			centerNode(d,originX,originY);
		}
		
	}

	function update(source) {
		console.info("UPDATE...");
		// Compute the new height, function counts total children of root node and sets tree height accordingly.
		// This prevents the layout looking squashed when new nodes are made visible or looking sparse when nodes are removed
		// This makes the layout more consistent.
		var levelWidth = [1];
		var childCount = function(level, n) {

			if (n.children && n.children.length > 0) {
				if (levelWidth.length <= level + 1) levelWidth.push(0);

				levelWidth[level + 1] += n.children.length;
				n.children.forEach(function(d) {
					childCount(level + 1, d);
				});
			}
		};
		childCount(0, root);
		var newHeight = d3.max(levelWidth) *25; // 25 pixels per line  
		tree = tree.size([newHeight, viewerWidth]);

		// Compute the new tree layout.
		var nodes = tree.nodes(root).reverse(),
		links = tree.links(nodes);

		// Set widths between levels based on maxLabelLength.
		nodes.forEach(function(d) {
			//d.y = (d.depth * (maxLabelLength * 5)); //maxLabelLength * 10px
			// alternatively to keep a fixed scale one can set a fixed depth per level
			// Normalize for fixed-depth by commenting out below line
			d.y = (d.depth * options.linkLength); 
		});

		// Update the nodes…
		var node = svgGroup.selectAll("g.node")
		.data(nodes, function(d) {
			return d.id || (d.id = ++i);
		});


		// Enter any new nodes at the parent's previous position.
		var nodeEnter = node.enter().append("g")
		// .call(dragListener)
		.attr("data-legend",function(d) { return d.type})
		.attr("data-legend-color",function(d) { return d.color})
		.attr("class", "node")
		.attr("transform", function(d) {
			return "translate(" + source.y0 + "," + source.x0 + ")";
		})
		.on('click', click);

		nodeEnter.append("circle")
		.attr('class', 'nodeCircle tooltipster')
		.attr("r", 0)
		.attr("data-name", function(d) {
			return d.name;
		})
		.style("stroke",function(d) {
			//return options.focusId==d.id?"black":"white";
			return d.color || "black";
		})
		.style("stroke-width",function(d) {
			//return options.focusId==d.id?3:0;
			return "3";
		})
		.style("fill", function(d) {
			
			if(d._children || d.children) return "white";
			else return d.color || "black";
			//return d._children ? "lightsteelblue" : "#fff";
		});
		/*.on('mouseover', function(d) {
			if(!hideTip)
			tip.show(d);
		})
		.on('mouseout', tip.hide);
		*/

		nodeEnter.append("text")
		.attr('class', 'nodeText')
		.attr("dx", function(d) {
			if(!d.children) return 10;
			if(d.name.length>=10) return "3em";
			else return "2em";
			
		})
		.attr("dy", function(d) {
			if(d.children) return "-.5em";
			else return ".35em";
		})
		.attr("text-anchor", function(d) {
			return d.children ? "end" : "start";
		})
		.text(function(d) {
			return d.name;
		})
		.style("fill-opacity", 0);

		// Change the circle fill depending on whether it has children and is collapsed
		node.select("circle.nodeCircle")
		.attr("r", circle_size)
		.style("fill", function(d) {
			return d._children? d.color:"white";
			//return d.color || "black";
			//return d._children ? "lightsteelblue" : "#fff";
		});

		// Transition nodes to their new position.
		var nodeUpdate = node.transition()
		.duration(duration)
		.attr("transform", function(d) {
			return "translate(" + d.y + "," + d.x + ")";
		});

		// Fade the text in
		nodeUpdate.select("text")
		.style("fill-opacity", 1)
		.attr("dx", function(d) {
			if(!d.children) return 10;
			if(d.name.length>=10) return "3em";
			else return "2em";
			
		})
		.attr("dy", function(d) {
			if(d.children) return "-.5em";
			else return ".35em";
		})
		.attr("text-anchor", function(d) {
			return d.children ? "end" : "start";
		});

		// Transition exiting nodes to the parent's new position.
		var nodeExit = node.exit().transition()
		.duration(duration)
		.attr("transform", function(d) {
			return "translate(" + source.y + "," + source.x + ")";
		})
		.remove();

		nodeExit.select("circle")
		.attr("r", 0);

		nodeExit.select("text")
		.style("fill-opacity", 0);

		// Update the links…
		var link = svgGroup.selectAll("path.link")
		.data(links, function(d) {
			return d.target.id;
		});

		// Enter any new links at the parent's previous position.
		link.enter().insert("path", "g")
		.attr("class", "link")
		.attr("stroke","grey")
		.attr("fill","none")
		.attr("d", function(d) {
			var o = {
					x: source.x0,
					y: source.y0
			};
			return diagonal({
				source: o,
				target: o
			});
		});

		// Transition links to their new position.
		link.transition()
		.duration(duration)
		.attr("d", diagonal);

		// Transition exiting nodes to the parent's new position.
		link.exit().transition()
		.duration(duration)
		.attr("d", function(d) {
			var o = {
					x: source.x,
					y: source.y
			};
			return diagonal({
				source: o,
				target: o
			});
		}).remove();

		// Stash the old positions for transition.
		nodes.forEach(function(d) {
			d.x0 = d.x;
			d.y0 = d.y;
		});

		//remove original legend
		baseSvg.select("g.legend").remove();
		//draw new legend
		baseSvg.append("g")
		.attr("class","legend")
		.attr("transform","translate(10,20)")
		.style("font-size","12px")
		.call(d3.legend);

		//update tooltip
		activeToolTip();

	}

	// Append a group which holds all nodes and which the zoom Listener can act upon.
	var svgGroup = baseSvg.append("g");

	// Define the root
	root = treeData;

	root.x0=viewerWidth / 2;
	root.y0=viewerHeight/2;

	// Layout the tree initially and center on the root node.
	//update(root);

	
	// Call visit function to populate nodeMap
	visit(-1,treeData,function(lv,d) {
		if(nodeMap[d.id]) {

		} else {
			nodeMap[d.id]=d;
		}
		return true;
	});
	console.info("nodeMap is as following:");
	console.dir(nodeMap);

	// Call visit function to establish maxLabelLength and set expand
	var findFocus=false;
	var focusLevel=-1;
	visit(-1,treeData, function(lv,d) {
		if(focusId && d.id==focusId) {
			focusNode=d;
		} 
		d._children=d.children;
		d.children=null;	
		return true;
	});

	if(!focusNode) focusNode=root;
	var pNode=focusNode;
	while(pNode!=null) {
		pNode.children=pNode._children;
		pNode._children=null;
		pNode=pNode._parent;
	}

	update(root);
	centerNode(root);
	if(focusNode) {
		centerNode(focusNode);
	}

}

/**end of definition wftree***/





});

