
window.onload = init;
var socket = new WebSocket(webscketurl);
socket.onmessage = onMessage;

function onMessage(event) {
    console.log(event.data);
    var obj = JSON.parse(event.data);
    if ("newDevice" in obj) {
        location.reload();
    }
    else if ("tempUpdate" in obj) {

        if (obj.device === currentEndpoint[0]) {
            tempArr.push(obj.tempUpdate);
            console.log(tempArr)
            if (tempArr.length > 20) {
                tempArr.splice(0, 1);
            }

            chart1.update(
                {
                    series: [{
                        name: 'Temperature',
                        data: tempArr
                    }]
                }
            );
        }
    } else {
        if (obj.device === currentEndpoint[0]) {
            document.getElementById("latlon").innerText = obj.lat + " " + obj.lon;
            document.getElementById("batteryLevel").innerText = obj.batteryLevel + "%";
            document.getElementById("batteryStatus").innerText = obj.batteryStatus;
            setTimeout(function () {

                document.getElementById('my-btn').children[0].classList.remove('fa-spin');
            }, 1000);
        }
    }
    }


    function init() {

        var btn = document.getElementById('my-btn');
        btn.onclick = function (event) {
            console.log("working");
            btn.children[0].classList.add('fa-spin');
            var obj = {};
            obj.action = "DeviceUpdate";
            obj.endpoint = currentEndpoint[0];
            socket.send(JSON.stringify(obj));
        }



}
