var ctx = document.getElementById('myChart').getContext('2d');
var tim = new Date();
var myChart = new Chart(ctx, {
    type: 'line',
    data: {
    //     labels: function(context){
    //       return Math.round((new Date()- tim)/1000);
    //     },
        datasets: [{label: 'Temperarture',
            data: tempArr,
            fill:false,
            lineTension: 0.8
        }]

    },
 options: {
     maintainAspectRatio: false
 }

});
