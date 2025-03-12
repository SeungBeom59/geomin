var stompClient = null;         // sockJs 클라이언트
var connected = false;          // 웹 소켓 연결 여부

$(document).ready(function (){

    $('#endWaitingBtn').addClass('off');

    var savedSessionId = sessionStorage.getItem('sessionId');

    if(savedSessionId){
        console.log("이미 저장된 세션ID가 존재합니다. " + savedSessionId);
    }
    else{
        console.log("새로운 세션ID 만들어짐.")
    }

    // 웹 소캣 연결
    if(!stompClient || !connected){
        connect(savedSessionId); // 웹 소켓 연결 시도.
    }

    ///////////////////////////////////
    // 웹 소켓 재사용하도록 만들어야 하는데,
    // 서버단에서 어떻게 할 것인지?
    ///////////////////////////////////

    ////////////////////////
    // 웹 소켓 통신 연결 시도 //
    function connect(savedSessionId){

        if(stompClient !== null && connected){
            console.log("서버와 실시간 통신 이미 연결됨.");
        }

        var savedSessionId = sessionStorage.getItem("sessionId");

        var socket = new SockJS("/ws");
        stompClient = Stomp.over(socket);

        if(!savedSessionId){
            console.log("세션 ID 없으므로, 새로운 소켓 연결을 시도.");
            // 웹 소켓 통신 미연결시, 연결 시도 진행
            stompClient.connect({}, onConnected , onError);
        }
        else{
            console.log("기존 세션 재사용하여 소켓 연결을 시도, 세션 ID :" + savedSessionId);
            stompClient.connect({sessionId : savedSessionId}, function(){
                connected = true;

                var departmentId = $('#departmentId').val();
                stompClient.subscribe("/sub/waiting/" + departmentId , onMessageReceived); // 특정 주제에 대해 구독한다.

            } , onError);
        }
    }
    // 웹 소켓 통신 연결된 경우
    function onConnected(){
        var sessionUrl = stompClient.ws._transport.url;  // 실제 세션 ID 가져오는 방식
        // ex : sessionId:ws://localhost:8080/ws/169/50hcbxrn/websocket 이런식
        var sessionId = sessionUrl.split("/")[5]; // 세션 id
        sessionStorage.setItem('sessionId', sessionId);  // 새로 고침 후에도 재사용 가능
        console.log("새로운 세션ID 저장됨." + sessionId);

        var departmentId = $('#departmentId').val();
        stompClient.subscribe("/sub/waiting/" + departmentId , onMessageReceived); // 특정 주제에 대해 구독한다.
    //    stompClient.send("/pub/waiting/" + departmentId);

        connected = true;
    }
});

//    stompClient.send(); // 컨트롤러 동작, (정보 요청)

// 웹 소켓 통신 에러
function onError(){
    console.log("서버와의 실시간 통신이 끊겼습니다.");

    connected = false;
}
// 서버로부터 받은 메세지 처리
function onMessageReceived(message){
    var response = JSON.parse(message.body);
    console.log("서버에서 받은 데이터 : " , response);

    // 사이드바 업데이트 해주도록.
    // 사이드바의 현재 버튼 활성화(진료대기 , 진료 완료환자) 여부에 따라 html 작업 필요
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// 로그아웃
function logout(){
    window.location.href = "http://localhost:8080/logout";
}

// 검색 버튼 클릭시 검색 함수 실행
$('#search-btn').click(function() {
    alert("클릭됨");
    search();
});

// 검색창에서 엔터 누를 시 검색 함수 실행
$('#search-bar').keypress(function(e){
    if(e.which === 13){
        search();
    }
})

// 검색 함수 : 팝업창에 환자 정보 띄우기
function search(){

    var patinetName = $('#search-bar').val();

    if(patinetName === null || patinetName == ''){
        alert('검색하실 환자이름을 입력해주세요.');
        return ;
    }

    var popup = window.open(
        '/patient-search/' + patinetName ,
        '환자 검색' ,
        'width=1055 , height=503, top=50, left=50, resizable=no');
}


/////////////////////////////////////////////////////////////////////////
// 서브메뉴 contextmenu 이벤트 설정
// waiting-type-menu에 마우스가 올라가면 추가 서브 메뉴의 활성화 여부 부여
$(".waiting-type-menu").mouseover(function(){
    $(this).children(".submenu").show();
});
$(".waiting-type-menu").mouseleave(function(){
    $(this).children(".submenu").hide();
});

// name-card 클래스를 갖는 접수대기환자카드에서 우클릭(contextmenu) 이벤트가 발생한다면
$(document).on('contextmenu', '.name-card', function(event) {
    event.preventDefault(); // 기본 contextmenu 이벤트 방지

    var waitingId = $(this).find('.waitingId').val();

    console.log('waitingId : ' , waitingId);
    // 접수대기환자의 id를 가진 채 만들어뒀던 context 메뉴를 마우스 위치 좌표에 보여줘라
    $('#custom-context-menu')
        .data(
            'waiting-id' , waitingId
        )
        .css({
        top: event.pageY + 'px',
        left: event.pageX + 'px'
    }).show();
});

// 클릭 이벤트가 발생할때, 그게 커스텀 콘텍스트 메뉴가 아니라면 메뉴는 비활성화해라.
// 여기서 length를 쓰는 이유는 클릭 이벤트 타켓은 클릭된 html dom을 알려주는데
// 거기서 closest() 메서드로 찾고자 하는 것이 몇개가 있는지 확인하는 방식이 됨.
// 해당 id의 태그가 존재한다면 1 아니라면 0.
$(document).on('click' , function(event){
    // console.log(event.target);
    if(!$(event.target).closest('#custom-context-menu').length){
        $('#custom-context-menu').hide();
    }
});

// 커스텀 메뉴에서 제공하는 서브메뉴에 li가 클릭될 경우 실행.
// 커스텀 메뉴에 갖고 있던 대기환자 id와 클릭된 태그의 문장을 가져와서 ajax 요청을 보낸다.
$('#custom-context-menu .submenu li').on('click' , function (){

    var waitingId = $('#custom-context-menu').data('waiting-id');
    var action = $(this).text();

    console.log('--------------------------클릭이벤트------------');
    console.log('waitingId = ' , waitingId);
    console.log('action = ' , action);

    var data = {
        waitingId : waitingId,
        action : action
    };

    $.ajax({
        url: "/waiting-modify",
        type: 'post',
        contentType: 'application/json; charset=utf-8',
        data: JSON.stringify(data),
        success: function (response){
            alert('성공');
            // 처리한 뒤에는 접수대기환자 최신화
            searchWaiting($('#waiting-current-page').val());
        } ,
        error : function (xhr, status, error){
            console.error('AJAX 요청 실패: ', status, error);
        }
    });
});

//////////////////////////////////////////////
//$(document).ready(function (){
//
//
//
//}