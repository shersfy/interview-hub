var interval = 2000; // 数据刷新频率
var memoChart = echarts.init(document.getElementById('memoChart'));
var option = {
		title: {
			show: false,
			text: 'jwatcher_memory'
		},
		tooltip : {
			formatter: "{b} <br/>{a} : {c}%"
		},
		toolbox: {
			feature: {
				saveAsImage: {
					show: false,
					type: 'jpg'
				}
			},
			top : 'bottom',
			itemGap: 20
		},
		series: [
			{
				name: '已使用',
				type: 'gauge',
				detail: {formatter:'{value}%'},
				data: [{value: 0, name: '物理内存'}]
			}
			]
};

setInterval(function () {
	$.ajax({
        url: 'memo',
        success:function(data){
            if (data.code != 200 ){
                alert("请求错误: "+url);
                return ;
            }
            
            option.tooltip.formatter = "{b} "+data.model.totalStr+"<br/>{a} "+data.model.usedStr+": {c}%";
            option.series[0].data[0].value = data.model.usedPercent.toFixed(2)-0;
            memoChart.setOption(option, true);
        }
    });
}, interval);

memoChart.setOption(option);