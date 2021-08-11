
function getDate() {

    var date = new Date();
    var hours = date.getHours();
    var minutes = date.getMinutes();
    var seconds = date.getSeconds();

//    var days=new Array("Воскресенье","Понедельник","Вторник",
//    "Среда","Четверг","Пятница","Суббота");

//    var months=new Array("января","февраля","марта","апреля","мая","июня",
//    "июля","августа","сентября","октября","ноября","декабря");

    var months=new Array("01","02","03","04","05","06",
        "07","08","09","10","11","12");

    if(seconds < 10)   {seconds = '0' + seconds;}

    document.getElementById('timeAndDate').innerHTML = hours + ':' + minutes + ':' + seconds + ' | '+ date.getDate() + '.' + months[date.getMonth()] + '.' + date.getFullYear();

}

setInterval(getDate, 0);