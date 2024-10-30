$(document).ready(function() {
    $('#search-bar').on('focus input', function() {
        $('#search-output').stop(true, true).slideDown(300); // 결과박스 애니메이션으로 표시
    });
    $('#search-bar').on('blur', function() {
        setTimeout(function() {
            $('#search-output').hide(); // 결과박스 애니메이션으로 숨김
        }, 200);
    });
    // search-nav 클릭 시 결과박스가 사라지지 않도록
    $('#search-nav').on('mousedown', function(e) {
        e.preventDefault(); // 클릭 시 focusout 방지
    });
    // values 클릭 시 결과박스 숨김
    $('#values').on('mousedown', function() {
        $('#search-output').hide(); // 클릭 시 결과박스 숨기기
    });


    // 클릭 이벤트 li에 발생시, 모든 clicked 클래스 삭제, 클릭된 객체에 클릭 클래스 추가.
    $("#search-nav li").click(function(){
        $("#search-nav li").removeClass("clicked");

        $(this).addClass("clicked");
    });


});

// 시간 지연처리위한 변수
let delayTimeout;

// 검색 함수
function search(){
    //
    var type = $('#search-nav li.clicked').attr('value');   // li에 value로 지정해둔 값을 가져온다.
    var keyword = $('#search-bar').val();  // 입력창에서 검색어를 가져옴

    // 검색어 길이가 3보다 작으면 바로 검색하지 않음.
    if(keyword.length < 3){
       return;  // 함수종료
    }

    // 기존에 있을지 모를 시간지연처리 이벤트를 없애준다.
    clearTimeout(delayTimeout);

    console.log('search type: ' , type);
    console.log('search keyword: ' , keyword);

    // 시간 지연처리 함수로 ajax를 실행하도록 한다. (지연 1초 , 1000ms)
    delayTimeout = setTimeout(function(){

    // 서버로 type과 keyword를 보낸다.
    $.ajax({
            url: '/treatment-search?type=' + type + '&keyword=' + keyword,
            type: 'post',
            contentType: 'application/json; charset=UTF8',
            success: function(response){
                console.log('성공');
            },
            error : function(xhr , status, error){
                console.log('ajax 실패' , status , error);
            }

        })

    } , 1000);
}


