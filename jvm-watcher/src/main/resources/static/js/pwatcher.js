$(function(){
	var interval = 2000;
	var pid = $("title").attr("pid");
	watcher.flush(pid);
	// 数据频率刷新
	if(watcher.intervalId<1){
		watcher.intervalId = watcher.flushInterval(interval, pid);
	}

	window.onbeforeunload= function(event) {
		return "";
	}
})

var watcher = {

	intervalId: 0,
	// heap memory
	initChart: function(title, subjects, xdata, ydata){
		var heapChart = echarts.init(document.getElementById(title.text+'Chart'));
		var option = {
				title : title,
				tooltip : {
					trigger: 'axis'
				},
				legend: {
					data:subjects
				},
				toolbox: {
					show : true,
					feature : {
						mark : {show: false},
						dataView : {show: false, readOnly: false},
						magicType : {show: false, type: ['line', 'bar', 'stack', 'tiled']},
						restore : {show: false},
						saveAsImage : {show: true}
					}
				},
				calculable : true,
				xAxis : [
					{
						type : 'category',
						boundaryGap : false,
						data : xdata
					}
				],
				yAxis : [
						{
							type : 'value'
						}
				],
				series : ydata
		};

		heapChart.setOption(option);
	},

	// 定时刷新
	flushInterval: function(interval, pid){
		var id = setInterval(function () {
			watcher.flush(pid);
		}, interval);
		return id;
	},
	// 刷新
	flush: function(pid){

		// get data
		$.ajax({
			url: basePath + '/process/local/data',
			data: {
				"pid": pid
			},
			success:function(result){
				if (result.code != 200 ){
					$.messager.alert('错误', result.msg, 'error');
					watcher.clean();
					return ;
				}
				if(result.model.length==0){
					return;
				}
				
				var xdata = new Array(result.model.length); // x时间元素
				
				var heapMax = 0;
				var heapSubjects = new Array(result.model[0].heapPools.length); // 类型元素
				var heapYdata = new Array(heapSubjects.length); // 数据元素

				var nonHeapMax = 0;
				var nonHeapSubjects = new Array(result.model[0].nonHeapPools.length); // 类型元素
				var nonHeapYdata = new Array(nonHeapSubjects.length); // 数据元素
				
				var usageSubjects = new Array('init', 'max', 'committed', 'used'); // 类型元素
				
				var heapDetailYdata = new Array(usageSubjects.length); // 数据元素
				for(var i=0; i<heapDetailYdata.length; i++){
					var yd = new Object();
					yd.name = usageSubjects[i];
					yd.type = 'line';
					yd.smooth = true;
					yd.itemStyle = {normal: {areaStyle: {type: 'default'}}};
					yd.data = new Array(xdata.length);
					heapDetailYdata[i] = yd;
				}
				
				var nonHeapDetailYdata = new Array(usageSubjects.length); // 数据元素
				for(var i=0; i<nonHeapDetailYdata.length; i++){
					var yd = new Object();
					yd.name = usageSubjects[i];
					yd.type = 'line';
					yd.smooth = true;
					yd.itemStyle = {normal: {areaStyle: {type: 'default'}}};
					yd.data = new Array(xdata.length);
					nonHeapDetailYdata[i] = yd;
				}
				
				
				for(var i=0; i<xdata.length; i++){
					var date = new Date(result.model[i].createTime);
					xdata[i] = (date.getMinutes()<10?"0":"")+date.getMinutes()+(date.getSeconds()<10?":0":":")+date.getSeconds();
					//heap
					for(var j=0; j<heapSubjects.length; j++){
						heapSubjects[j] = result.model[i].heapPools[j].name;
						if(heapYdata[j] == undefined){
							var yd = new Object();
							yd.name = heapSubjects[j];
							yd.type = 'line';
							yd.smooth = true;
							yd.itemStyle = {normal: {areaStyle: {type: 'default'}}};
							yd.data = new Array(xdata.length);
							heapYdata[j] = yd;
						}
						var init = result.model[i].heapPools[j].init/1024.0/1024;
						var max  = result.model[i].heapPools[j].max/1024.0/1024;
						var committed  = result.model[i].heapPools[j].committed/1024.0/1024;
						var used = result.model[i].heapPools[j].used/1024.0/1024;
						if(heapSubjects[j]=='heap'){
							heapYdata[j].data[i] = (init>max?init:max).toFixed(3);
							if(heapMax==0){
								heapMax = heapYdata[j].data[i];
							}
						} else {
							heapYdata[j].data[i] = used.toFixed(3);
						}
					}
					// non-heap
					for(var j=0; j<nonHeapSubjects.length; j++){
						nonHeapSubjects[j] = result.model[i].nonHeapPools[j].name;
						if(nonHeapYdata[j] == undefined){
							var yd = new Object();
							yd.name = nonHeapSubjects[j];
							yd.type = 'line';
							yd.smooth = true;
							yd.itemStyle = {normal: {areaStyle: {type: 'default'}}};
							yd.data = new Array(xdata.length);
							nonHeapYdata[j] = yd;
						}
						var init = result.model[i].nonHeapPools[j].init/1024.0/1024;
						var max  = result.model[i].nonHeapPools[j].max/1024.0/1024;
						var committed  = result.model[i].nonHeapPools[j].committed/1024.0/1024;
						var used = result.model[i].nonHeapPools[j].used/1024.0/1024;
						if(nonHeapSubjects[j]=='non-heap'){
							nonHeapYdata[j].data[i] = (init>committed?init:committed).toFixed(3);
							if(nonHeapMax==0){
								nonHeapMax = nonHeapYdata[j].data[i];
							}
						} else {
							nonHeapYdata[j].data[i] = used.toFixed(3);
						}
					}
					// heap-detail
					for(var j=0; j<result.model[i].heapPools.length; j++){
						heapSubjects[j] = result.model[i].heapPools[j].name;
						if(heapSubjects[j]!='heap'){
							continue;
						}
						
						var init = result.model[i].heapPools[j].init/1024.0/1024;
						var max  = result.model[i].heapPools[j].max/1024.0/1024;
						var committed  = result.model[i].heapPools[j].committed/1024.0/1024;
						var used = result.model[i].heapPools[j].used/1024.0/1024;
						
						heapDetailYdata[0].data[i] = init.toFixed(3);
						heapDetailYdata[1].data[i] = max.toFixed(3);
						heapDetailYdata[2].data[i] = committed.toFixed(3);
						heapDetailYdata[3].data[i] = used.toFixed(3);
					}
					// non-heap-detail
					for(var j=0; j<result.model[i].nonHeapPools.length; j++){
						nonHeapSubjects[j] = result.model[i].nonHeapPools[j].name;
						if(nonHeapSubjects[j]!='non-heap'){
							continue;
						}
						
						var init = result.model[i].nonHeapPools[j].init/1024.0/1024;
						var max  = result.model[i].nonHeapPools[j].max/1024.0/1024;
						var committed  = result.model[i].nonHeapPools[j].committed/1024.0/1024;
						var used = result.model[i].nonHeapPools[j].used/1024.0/1024;
						
						nonHeapDetailYdata[0].data[i] = init.toFixed(3);
						nonHeapDetailYdata[1].data[i] = max.toFixed(3);
						nonHeapDetailYdata[2].data[i] = committed.toFixed(3);
						nonHeapDetailYdata[3].data[i] = used.toFixed(3);
					}
					
				}
				
				// 渲染图表
				// heap
				var heap = {
						text: 'heap',
						subtext: heapMax+" M"
					};
				watcher.initChart(heap, heapSubjects, xdata, heapYdata);
				// non-heap
				var nonHeap = {
						text: 'non-heap',
						subtext: nonHeapMax+" M"
					};
				watcher.initChart(nonHeap, nonHeapSubjects, xdata, nonHeapYdata);
				// heap-detail
				var heapDetail = {
						text: 'heap-detail',
						subtext: heapMax+" M"
				};
				watcher.initChart(heapDetail, usageSubjects, xdata, heapDetailYdata);
				// non-heap-detail
				var nonHeapDetail = {
						text: 'non-heap-detail',
						subtext: nonHeapMax+" M"
				};
				watcher.initChart(nonHeapDetail, usageSubjects, xdata, nonHeapDetailYdata);
				
			}
			
		});
	},
	// 清除定时任务
	clean: function(){
		clearInterval(watcher.intervalId);
		watcher.intervalId = 0;
	}

}
