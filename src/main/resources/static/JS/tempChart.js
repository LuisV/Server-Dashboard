var chart1 = Highcharts.chart('chartContainer',{
    chart: {
        renderTo: 'container',
        type: 'line'
    },
    title: {
        text: 'Operating Temperature'
    },
    xAxis: {
        type:'linear',
        tickInterval: 1
    },
    yAxis: {
        title: {
            text: 'Degrees Celsius'
        },
        labels:{
            formatter: function(){
                return this.value + "°C"
            }
        },
        min: 0,
        max: 100
    },
    series: [{
        name: 'Temperature',
        data: tempArr
    }]


});