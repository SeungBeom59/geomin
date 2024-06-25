$(document).ready(function (){
    $('#waiting-symptoms').attr('placeholder' , '미입력시, \'의사와 상담시 증상을 설명하고자 함.\'\n으로 작성됩니다.');

    var today = new Date();
    // var time = today.getHours().toString().padStart(2, '0') + ":"
    var time = today.getFullYear() + "/"
             + (today.getMonth() + 1).toString().padStart(2, '0') + "/"
             + today.getDate().toString().padStart(2 , '0');

    $('#waiting-symptoms-box > div:first-child > span:nth-child(2)').text(time);
});

// window.addEventListener('beforeunload' , function before (e){
//
//     e.preventDefault(); // 기본 동작 방지
//     e.returnValue = '';
//
//     // var msg = '진료 접수를 취소하시겠습니까?';
//     //
//     // e.returnValue = msg; // 크롬 설정
//     //
//     // return msg;
// });

