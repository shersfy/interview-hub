$(function(){
//	var initMemo = JSON.parse($('#memoChart').attr('memo'));
//	var initCpu  = JSON.parse($('#cpuChart').attr('cpu'));
	var heap = 10;
	watcher.initHeapChart(heap);

	// 数据频率刷新
//	if(header.intervalId<1){
//	header.intervalId = capacity.flush(2000);
//	}
	window.onbeforeunload= function(event) {
		return "";
	}
})

var watcher = {
	// heap memory
	initHeapChart: function(heap){
		var heapChart = echarts.init(document.getElementById('heapChart'));
		var types = new Array('heap-eden','heap-serviu','non-heap');
		var option = {
				title : {
					text: 'heap',
					subtext: ''
				},
				tooltip : {
					trigger: 'axis'
				},
				legend: {
					data:types
				},
				toolbox: {
					show : false,
					feature : {
						mark : {show: true},
						dataView : {show: true, readOnly: false},
						magicType : {show: true, type: ['line', 'bar', 'stack', 'tiled']},
						restore : {show: true},
						saveAsImage : {show: true}
					}
				},
				calculable : true,
				xAxis : [
				         {
				        	 type : 'category',
				        	 boundaryGap : false,
				        	 data : ['周一','周二','周三','周四','周五','周六','周日']
				         }
				         ],
				         yAxis : [
				                  {
				                	  type : 'value'
				                  }
				                  ],
				                  series : [
				                            {
				                            	name:types[0],
				                            	type:'line',
				                            	smooth:true,
				                            	itemStyle: {normal: {areaStyle: {type: 'default'}}},
				                            	data:[10, 12, 21, 54, 260, 830, 710]
				                            },
				                            {
				                            	name:types[1],
				                            	type:'line',
				                            	smooth:true,
				                            	itemStyle: {normal: {areaStyle: {type: 'default'}}},
				                            	data:[30, 182, 434, 791, 390, 30, 10]
				                            },
				                            {
				                            	name:types[2],
				                            	type:'line',
				                            	smooth:true,
				                            	itemStyle: {normal: {areaStyle: {type: 'default'}}},
				                            	data:[1320, 1320, 1320, 1320, 1320, 1320, 1320]
				                            }
				                            ]
		};

		heapChart.setOption(option);
	}


}
