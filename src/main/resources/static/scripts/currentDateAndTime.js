
function getDate() {

    var date = new Date();
    var hours = date.getHours();
    var minutes = date.getMinutes();
    var seconds = date.getSeconds();
    var month = date.getMonth();

    if(seconds < 10) {seconds = '0' + seconds;}
    if(minutes < 10) {minutes = '0' + minutes;}
    if(month < 10) {month = '0' + month;}

    document.getElementById('timeAndDate').innerHTML = hours + ':' + minutes + ':' + seconds + ' | '+ date.getDate() + '.' + month + '.' + date.getFullYear();

}

setInterval(getDate, 0);