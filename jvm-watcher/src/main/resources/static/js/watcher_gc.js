$(function(){
	var interval = 2000;
	var url = $("title").attr("url");
	
	watcherGC.flush(url);
	// 数据频率刷新
	if(watcherGC.intervalId<1){
		watcherGC.intervalId = watcherGC.flushInterval(interval, url);
	}
	
	$(".easyui-accordion").accordion('getSelected').panel('collapse');
	
	window.onbeforeunload= function(event) {
		return "";
	}
})

function Ydata(name, xlen) {
	this.name = name;
	this.type = 'line';
	
	this.smooth = true;
	this.data = new Array(xlen);
};

var watcherGC = {

	intervalId: 0,
	// gc chart
	initChart: function(title, subjects, xdata, ydata){
		var gcChart = echarts.init(document.getElementById(title.text+'Chart'));
		var option = {
				title : title,
				tooltip : {
					trigger: 'axis',
					formatter: ""
				},
				legend: {
					data:subjects
				},
				toolbox: {
					show : true,
					feature : {
						saveAsImage : {show: true, title: "save"}
					}
				},
				xAxis : [
					{
						type : 'category',
						boundaryGap : false,
						data : xdata
					}
				],
				yAxis : {
							type : 'value'
						},
				series : ydata
		};

		gcChart.setOption(option);
	},

	// 定时刷新
	flushInterval: function(interval, url){
		var id = setInterval(function () {
			watcherGC.flush(url);
		}, interval);
		return id;
	},
	// 刷新
	flush: function(url){

		// get data
		$.ajax({
			url: basePath + '/process/gchart',
			data: {
				"url": url
			},
			success:function(result){
				if (result.code != 200 ){
					$.messager.alert('错误', result.msg, 'error');
					watcherGC.clean();
					return ;
				}
				if(result.model==''||result.model=='null'){
					return;
				}
				
				var subjects = result.model.subjects; // 类型元素
				var xdata    = result.model.xdata; // x时间元素
				var ydata    = new Array(subjects.length); // 数据元素

				for(var i=0; i<ydata.length; i++){
					ydata[i] = new Ydata(subjects[i], xdata.length);
				}
				
				for(var i=0; i<subjects.length; i++){
					for(var j=0; j<xdata.length; j++){
						ydata[i].data[j] = parseInt(result.model.ydata[i][j].percent*100);
					}
				}
				
				// 渲染图表
				// gc chart
				var gc = {
						text: 'GC-Chart',
						subtext: 'GC回收率 %'
					};
				watcherGC.initChart(gc, subjects, xdata, ydata);
				
			}
			
		});
	},
	// 清除定时任务
	clean: function(){
		clearInterval(watcherGC.intervalId);
		watcherGC.intervalId = 0;
	}

}
