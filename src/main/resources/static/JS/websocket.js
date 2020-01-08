
window.onload = init;
var socket = new WebSocket("ws://3.136.212.23:8080/dashboard/actions");
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
        // myChart.update()
        //location.reload();
        //var content = document.getElementById("content");
        //var deviceDiv = document.createElement("div");
        //deviceDiv.innerHTML = event.data;
        // content.appendChild(deviceDiv);
        // var device = JSON.parse(event.data);
        // if (device.action =var btn = document.getElementById('my-btn');== "add") {
        //     printDeviceElement(device);
        // }
        // if (device.action === "remove") {
        //     document.getElementById(device.id).remove();
        //     //device.parentNode.removeChild(device);
        // }
        // if (device.action === "toggle") {
        //     var node = document.getElementById(device.id);
        //     var statusText = node.children[2];
        //     if (device.status === "On") {
        //         statusText.innerHTML = "Status: " + device.status + " (<a href=\"#\" OnClick=toggleDevice(" + device.id + ")>Turn off</a>)";
        //     } else if (device.status === "Off") {
        //         statusText.innerHTML = "Status: " + device.status + " (<a href=\"#\" OnClick=toggleDevice(" + device.id + ")>Turn on</a>)";
        //     }
        // }
    }

    function addDevice(name, type, description) {
        var DeviceAction = {
            action: "add",
            name: name,
            type: type,
            description: description
        };
        socket.send(JSON.stringify(DeviceAction));
    }

    function removeDevice(element) {
        var id = element;
        var DeviceAction = {
            action: "remove",
            id: id
        };
        socket.send(JSON.stringify(DeviceAction));
    }

    function toggleDevice(element) {
        var id = element;
        var DeviceAction = {
            action: "toggle",
            id: id
        };
        socket.send(JSON.stringify(DeviceAction));
    }

    function printDeviceElement(device) {
        var content = document.getElementById("content");

        var deviceDiv = document.createElement("div");
        deviceDiv.setAttribute("id", device.id);
        deviceDiv.setAttribute("class", "device " + device.type);
        content.appendChild(deviceDiv);

        var deviceName = document.createElement("span");
        deviceName.setAttribute("class", "deviceName");
        deviceName.innerHTML = device.name;
        deviceDiv.appendChild(deviceName);

        var deviceType = document.createElement("span");
        deviceType.innerHTML = "<b>Type:</b> " + device.type;
        deviceDiv.appendChild(deviceType);

        var deviceStatus = document.createElement("span");
        if (device.status === "On") {
            deviceStatus.innerHTML = "<b>Status:</b> " + device.status + " (<a href=\"#\" OnClick=toggleDevice(" + device.id + ")>Turn off</a>)";
        } else if (device.status === "Off") {
            deviceStatus.innerHTML = "<b>Status:</b> " + device.status + " (<a href=\"#\" OnClick=toggleDevice(" + device.id + ")>Turn on</a>)";
            //deviceDiv.setAttribute("class", "device off");
        }
        deviceDiv.appendChild(deviceStatus);

        var deviceDescription = document.createElement("span");
        deviceDescription.innerHTML = "<b>Comments:</b> " + device.description;
        deviceDiv.appendChild(deviceDescription);

        var removeDevice = document.createElement("span");
        removeDevice.setAttribute("class", "removeDevice");
        removeDevice.innerHTML = "<a href=\"#\" OnClick=removeDevice(" + device.id + ")>Remove device</a>";
        deviceDiv.appendChild(removeDevice);
    }

    function formSubmit() {
        var form = document.getElementById("addDeviceForm");
        var name = form.elements["device_name"].value;
        var type = form.elements["device_type"].value;
        var description = form.elements["device_description"].value;
        hideForm();
        document.getElementById("addDeviceForm").reset();
        addDevice(name, type, description);
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
