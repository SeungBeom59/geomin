// 시작시 활력징후 입력표 초기화
$(document).ready(function (){
    var date = new Date();
    var today =  date.getFullYear() + "-" + ("0"+(date.getMonth()+1)).slice(-2) + "-" + ("0"+date.getDate()).slice(-2);

    var $tr = $('#vital-sign-table tbody tr:nth-child(2)');
    $tr.find('td').eq(0).find('input').val(today);
    $tr.find('td').eq(1).find('input').val(0);
    $tr.find('td').eq(2).find('input').val(0);
    $tr.find('td').eq(3).find('input').val(0);
    $tr.find('td').eq(4).find('input').val(0);
    $tr.find('td').eq(5).find('input').val(0);
    $tr.find('td').eq(6).find('input').val(0);
});

// 로그아웃
function logout(){
    window.location.href = "http://localhost:8080/logout";
}

// 검색 버튼 클릭시 검색 함수 실행
$('#search-btn').click(function() {
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

// 자식(팝업)으로 부터 온 값 수신용 함수
function receivePatientInfo(patient) {
    var patientObj = JSON.parse(patient);  // json형태로 넘겨줬기 때문에 parse

    console.log(patientObj);

    // 접수 환자 정보 칸에 값 넣어주기
    insertPatient(patientObj);
    // 검색 입력창 초기화
    $('#search-bar').val("");
}

// 환자 정보 초기화 함수
function clearPatient(){

    // 활력징후 테이블 초기화
    clearVitals();

    // 주소검색 버튼 보이기
    $('#post-search-btn').css('display' , 'inline-block');
    // 환자id 삭제하기
    $('#patientId').val("");

    // form의 적절한 필드에 넣어주고 readonly로 변경
    $('#patient_name').val("").attr('readonly', false);
    $('#identify').val("").attr('readonly', false);

    // $('#gender').val("").attr('readonly', false);
    $('#gender').val(true).attr('disabled' , false);

    $('#address').val("");
    $('#post-code').val("");
    $('#extraAddress').val("");
    $('#address-detail').val("").attr('readonly', false);
    $('#age').val("").attr('readonly', false);
    $('#blood-type').val("").attr('readonly', false);
    $('#phone').val("").attr('readonly', false);
    $('#emergency-phone').val("").attr('readonly', false);

    // 수정 버튼 비활성화 | 등록버튼 활성화
    $('#patient-modify-btn').css('display' , 'none');
    $('#patient-btn').css('display', 'inline-block');

    $('#vital-sign-cancel-btn').css('display' , 'none');
}

// 접수 환자 정보 칸 값 넣기 및 readonly와 수정 버튼 활성화
function insertPatient(patientObj){

    // 주소검색 버튼 감추기
    $('#post-search-btn').css('display' , 'none');
    // 환자id 몰래 넣어놓기
    $('#patientId').val(patientObj.patientId);

    // form의 적절한 필드에 넣어주고 readonly로 변경
    $('#patient_name').val(patientObj.patientName).attr('readonly', true);
    $('#identify').val(patientObj.identify).attr('readonly', true);

    // var gender = patientObj.gender? '남' : '여';  // true , false 값이기 때문에 삼항연산자 이용

    $('#gender').val(patientObj.gender.toString()).attr('disabled', true);
    $('#address').val(patientObj.address).attr('readonly', true);
    $('#address-detail').val(patientObj.addressDetail).attr('readonly', true);
    $('#post-code').val(patientObj.postCode).attr('readonly', true);
    $('#age').val(patientObj.age).attr('readonly', true);
    $('#blood-type').val(patientObj.bloodType).attr('readonly', true);
    $('#phone').val(patientObj.phone).attr('readonly', true);
    $('#emergency-phone').val(patientObj.emergencyPhone).attr('readonly', true);

    // 수정 버튼 활성화 | 등록버튼 비활성화
    $('#patient-modify-btn').css('display' , 'inline-block');
    $('#patient-btn').css('display', 'none');
    $('#patient-cancel-btn').css('display' , 'none');
    $('#patient-update-btn').css('display' , 'none');

    searchVitals();

}

// 활력징후 검색
function searchVitals(){

    $('#vital-sign-cancel-btn').css('display' , 'none');
    $('#vital-sign-update-btn').css('display' , 'none');

    var data = {
        patientId : $('#patientId').val()
    };

    var page = $('#vitalPage').val();

    $.ajax({
        url: '/vitals-search?page=' + page,
        type: 'POST',
        data: JSON.stringify(data),
        contentType: 'application/json; charset=utf-8',
        success: function (response){
            console.log(response);
            if(response.content.length === 0){
                // 활력징후 결과 없을시
                clearVitals();
                return;
            }
            insertVitals(response);
        },
        error: function(xhr, status, error) {
            console.error('AJAX 요청 실패: ' , status , error);
        }
    });
}

// 활력징후 값 넣기
function insertVitals(response){

    console.log("insertVitals");
    // array 안에 담긴 json 가져오기

    $('#vital-sign-modify-btn').css('display' , 'inline-block');
    $('#vital-sign-add-btn').css('display' , 'inline-block');
    $('#vital-sign-btn').css('display' , 'none');
    $('#vital-sign-update-btn').css('display' , 'none');
    $('#vital-sign-cancel-btn').css('display' , 'none');

    var vitals = response.content.at(0);

    console.log(response);

    console.log(vitals);

    $('#vitalId').val(vitals.vitalId);  // vitalId 저장
    console.log("vitalId = " + $('#vitalId').val());

    // var vitalDate = vitals.vitalDate.split('T')[0]; // 필요없는 시분초 빼고 날짜만 저장

    var vitalDate = vitals.vitalDate.split(' ')[0];
    var $tr = $('#vital-sign-table tbody tr:nth-child(2)');

    $tr.find('td').eq(0).find('input').val(vitalDate).attr('readonly' , true);
    $tr.find('td').eq(1).find('input').val(vitals.weight).attr('readonly' , true);
    $tr.find('td').eq(2).find('input').val(vitals.height).attr('readonly' , true);
    $tr.find('td').eq(3).find('input').val(vitals.systolicBlood).attr('readonly' , true);
    $tr.find('td').eq(4).find('input').val(vitals.diastolicBlood).attr('readonly' , true);
    $tr.find('td').eq(5).find('input').val(vitals.temperature).attr('readonly' , true);
    $tr.find('td').eq(6).find('input').val(vitals.pulse).attr('readonly' , true);

    vitalsPageing(response);

}

// 활력징후 값 초기화
function clearVitals(){

    // 버튼 보이기 설정
    $('#vital-sign-modify-btn').css('display' , 'none');
    $('#vital-sign-add-btn').css('display' , 'none');
    $('#vital-sign-btn').css('display' , 'inline-block');
    $('#vital-sign-cancel-btn').css('display', 'none');
    $('#vital-sign-pageing').hide();

    // 활력징후 page 번호, 활력징후 id 삭제
    $('#vitalId').val("");
    $('#vitalPage').val("");

    var date = new Date();
    var today =  date.getFullYear() + "-" + ("0"+(date.getMonth()+1)).slice(-2) + "-" + ("0"+date.getDate()).slice(-2);

    var $tr = $('#vital-sign-table tbody tr:nth-child(2)');
    $tr.find('td').eq(0).find('input').val(today).attr('readonly' , false);
    $tr.find('td').eq(1).find('input').val(0).attr('readonly' , false);
    $tr.find('td').eq(2).find('input').val(0).attr('readonly' , false);
    $tr.find('td').eq(3).find('input').val(0).attr('readonly' , false);
    $tr.find('td').eq(4).find('input').val(0).attr('readonly' , false);
    $tr.find('td').eq(5).find('input').val(0).attr('readonly' , false);
    $tr.find('td').eq(6).find('input').val(0).attr('readonly' , false);

}

// 활력징후 표 페이징
function vitalsPageing(response){

    if (!response || !response.pageable || response.totalElements <= 1) {
        $('#vital-sign-pageing').hide();
        return;
    }

    console.log("페이징 response : ");
    console.log(response);

    $('#vital-sign-pageing').css('display','flex'); // 페이징 보여주기

    var totalElements = response.totalElements; // 총 레코드 갯수
    var totalPages = response.totalPages;       // 총 페이지 갯수
    var size = response.size;               // 보여줄 레코드 수
    var currentPage = response.pageable.pageNumber;      // 현재 페이지
    var patientId = response.content.at(0).patientId;

    $('#vitalPage').val(currentPage);

    console.log("patientId = " + patientId);

    console.log("totalPages = " + totalPages + ','
        + "totalElements =" + totalElements + ','
        + "size = " + size + ','
        + "currentPage = " + currentPage);

    if(currentPage === 0){
        $('#prev-page').hide();
    }
    else {
        $('#prev-page').css('display' , 'flex');
    }

    if(currentPage + 1  === totalPages){
        $('#next-page').hide();
    }
    else {
        $('#next-page').css('display' , 'flex');
    }

    $('#vital-sign-pageing li').not('#prev-page, #next-page').remove(); // 기존 페이지 번호 삭제

    var startPage = Math.max(0, Math.floor(currentPage / 3) * 3);
    var endPage = Math.min(startPage + 3, totalPages);

    console.log("startPage = " + startPage);
    console.log("endPage = " + endPage);

    for(var i = startPage; i < endPage; i++) {
        var pageItem = $('<li><a class="page-link">' + (i + 1) + '</a></li>');

        if(i === currentPage){
            pageItem.find('.page-link').addClass('active');
        }

        if(i === currentPage && currentPage === endPage){
            pageItem.find('.page-link').css('margin-right' , '30px !important');
        }

        pageItem.insertBefore('#next-page');
    }

    // 모든 페이징 버튼 클릭 이벤트 제거 후 이벤트 부여
    $('.page-link').off('click').click(function (e) {
        e.preventDefault();
        var page = parseInt($(this).text()) - 1;
        loadVitals(patientId, page);
    });

    $('#prev-page').off('click').click(function (e){
        e.preventDefault();
        var page = currentPage - 1;
        loadVitals(patientId, page);
    })

    $('#next-page').off('click').click(function (e){
        e.preventDefault();
        var page = currentPage + 1;
        loadVitals(patientId, page);
    })

}





// 활력징후 페이지 지정 검색
function loadVitals(patientId, page){
    var data = { patientId: patientId};

    $.ajax({
        url: '/vitals-search?page=' + page,
        type: 'POST',
        data: JSON.stringify(data),
        contentType: 'application/json; charset=utf-8',
        success: function (response) {
            if (!response || response.content.length === 0) {
                clearVitals();
                $('#vital-sign-pageing').hide();
                return;
            }
            insertVitals(response);
        },
        error: function (xhr, status, error) {
            console.error('AJAX 요청 실패: ', status, error);
        }
    });
}

// 활력징후 새로 등록
function addVital(){

    if($('#patientId').val() === null || $('#patientId').val() === ""){
        alert('환자 정보가 없습니다.');
        return;
    }

    if(checkVital()){
        alert('유효한 데이터가 없습니다.');
        return;
    }


    var $tr = $('#vital-sign-table tbody tr:nth-child(2)');

    var today = new Date();
    var time = today.getHours().toString().padStart(2, '0') + ":"
        + today.getMinutes().toString().padStart(2, '0')
        + ":" + today.getSeconds().toString().padStart(2, '0');
    var now = $tr.find('td').eq(0).find('input').val() + " " + time;

    var data = {
        patientId : $('#patientId').val(),
        vitalDate : now,
        weight : $tr.find('td').eq(1).find('input').val(),
        height : $tr.find('td').eq(2).find('input').val(),
        systolicBlood : $tr.find('td').eq(3).find('input').val(),
        diastolicBlood : $tr.find('td').eq(4).find('input').val(),
        temperature : $tr.find('td').eq(5).find('input').val(),
        pulse : $tr.find('td').eq(6).find('input').val()
    }

    console.log(data);

    $.ajax({
        url: '/vitals-add',
        type: 'POST',
        data: JSON.stringify(data),
        contentType: 'application/json; charset=utf-8',
        success: function (response) {
            alert('등록되었습니다.');
            if (!response || response.content.length === 0) {
                clearVitals();
                $('#vital-sign-pageing').hide();
                return;
            }
            insertVitals(response);
        },
        error: function (xhr, status, error) {
            alert('등록 실패');
            console.error('AJAX 요청 실패: ', status, error);
        }
    });
}

// 활력징후 수정시 입력표 readonly 해제 및 버튼 변경
function modifyVital(){

    $('#vital-sign-modify-btn').css('display' , 'none');
    $('#vital-sign-add-btn').css('display' , 'none');
    $('#vital-sign-btn').css('display' , 'none');
    $('#vital-sign-update-btn').css('display' , 'inline-block');
    $('#vital-sign-cancel-btn').css('display', 'inline-block');

    var $tr = $('#vital-sign-table tbody tr:nth-child(2)');
    $tr.find('td').eq(0).find('input').attr('readonly' , true);
    $tr.find('td').eq(1).find('input').attr('readonly' , false);
    $tr.find('td').eq(2).find('input').attr('readonly' , false);
    $tr.find('td').eq(3).find('input').attr('readonly' , false);
    $tr.find('td').eq(4).find('input').attr('readonly' , false);
    $tr.find('td').eq(5).find('input').attr('readonly' , false);
    $tr.find('td').eq(6).find('input').attr('readonly' , false);

}

// 활력징후 업데이트|수정 요청
function updateVital(){

    if(checkVital()){
        alert('유효한 데이터가 없습니다.');
        return;
    }

    var $tr = $('#vital-sign-table tbody tr:nth-child(2)');

    var date = new Date();
    var today = date.getFullYear() + "-"
                + (date.getMonth() + 1).toString().padStart(2 , '0') + "-"
                + date.getDate().toString().padStart(2, '0');
    var time = date.getHours().toString().padStart(2, '0') + ":"
        + date.getMinutes().toString().padStart(2, '0')
        + ":" + date.getSeconds().toString().padStart(2, '0');
    var now = today + " " + time;

    var data = {
        vitalId : $('#vitalId').val(),
        patientId : $('#patientId').val(),
        weight : $tr.find('td').eq(1).find('input').val(),
        height : $tr.find('td').eq(2).find('input').val(),
        systolicBlood : $tr.find('td').eq(3).find('input').val(),
        diastolicBlood : $tr.find('td').eq(4).find('input').val(),
        temperature : $tr.find('td').eq(5).find('input').val(),
        pulse : $tr.find('td').eq(6).find('input').val(),
        modifyDate : now,
        vitalModifier : $('#userId').val()
    }

    $.ajax({
        url: '/vitals-update',
        type: 'POST',
        data: JSON.stringify(data),
        contentType: 'application/json; charset=utf-8',
        success: function (response){

            if (!response || response.content.length === 0) {
                clearVitals();
                $('#vital-sign-pageing').hide();
                return;
            }
            insertVitals(response);
        },
        error: function (xhr, status, error) {
            alert('업데이트 실패');
            console.error('AJAX 요청 실패: ', status, error);
        }
    });
}

// 주소 검색
function daumPostcode(){

    new daum.Postcode({
        oncomplete: function(data) {
            // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

            // 각 주소의 노출 규칙에 따라 주소를 조합한다.
            // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
            var addr = ''; // 주소 변수
            var extraAddr = ''; // 참고항목 변수

            //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
            if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
                addr = data.roadAddress;
            } else { // 사용자가 지번 주소를 선택했을 경우(J)
                addr = data.jibunAddress;
            }

            // 사용자가 선택한 주소가 도로명 타입일때 참고항목을 조합한다.
            if(data.userSelectedType === 'R'){
                // 법정동명이 있을 경우 추가한다. (법정리는 제외)
                // 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
                if(data.bname !== '' && /[동|로|가]$/g.test(data.bname)){
                    extraAddr += data.bname;
                }
                // 건물명이 있고, 공동주택일 경우 추가한다.
                if(data.buildingName !== '' && data.apartment === 'Y'){
                    extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                }
                // 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
                if(extraAddr !== ''){
                    extraAddr = ' (' + extraAddr + ')';
                }
                // 조합된 참고항목을 해당 필드에 넣는다.
                document.getElementById("extraAddress").value = extraAddr;

            } else {
                document.getElementById("extraAddress").value = '';
            }

            // 우편번호와 주소 정보를 해당 필드에 넣는다.
            document.getElementById('post-code').value = data.zonecode;
            document.getElementById("address").value = addr;
            // 커서를 상세주소 필드로 이동한다.
            document.getElementById("address-detail").focus();
        }
    }).open();
}

// 환자 정보 추가
function addPatient(){

    if(checkPatient()){
        alert('[필수]이름 또는 주민등록번호에 데이터가 없습니다.');
        return;
    }

    data = {
        patientId : $('#patientId').val(),
        patientName : $('#patient_name').val(),
        identify : $('#identify').val(),
        gender : $('#gender').val(),
        address : $('#address').val(),
        addressDetail : $('#address-detail').val(),
        postCode : $('#post-code').val(),
        extraAddress : $('#extraAddress').val(),
        age : $('#age').val(),
        bloodType : $('#blood-type').val(),
        phone : $('#phone').val(),
        emergencyPhone : $('#emergency-phone').val()
    }
    // 주민등록 검증
    var regex = /^\d{6}-\d{7}$/;
    if(!regex.test(data.identify)){
        alert('유효한 주민등록번호가 아닙니다.');
        return;
    }

    console.log(data);

    if(data.patientId !== "" && data.patientId !== "0" && data.patientId !== null){
        $.ajax({
            url : '/patient-update',
            type: 'POST',
            data: JSON.stringify(data),
            contentType : 'application/json; charset=utf-8',
            success: function (response){
                insertPatient(response);
            },
            error : function (xhr, status, error){
                alert('환자 정보 수정 실패');
                console.error('ajax 요청 실패 : ' , status , error);
            }
        })

    }
    else {
        $.ajax({
            url : '/patient-add',
            type: 'POST',
            data: JSON.stringify(data),
            contentType: 'application/json; charset=utf-8',
            success: function (response){
                alert('환자 등록 성공');
                insertPatient(response);
            },
            error: function (xhr, status, error) {
                alert('환자 등록 실패');
                console.error('AJAX 요청 실패: ', status, error);
            }

        });
    }
}

function modifyPatient(){

    // 주소검색 버튼 보이기
    $('#post-search-btn').css('display' , 'inline-block');

    // form의 input들 readonly 해제
    $('#patient_name').attr('readonly', false);
    $('#identify').attr('readonly', false);
    $('#gender').attr('disabled' , false);
    $('#address-detail').attr('readonly', false);
    $('#age').attr('readonly', false);
    $('#blood-type').attr('readonly', false);
    $('#phone').attr('readonly', false);
    $('#emergency-phone').attr('readonly', false);

    // 수정 버튼 비활성화 | 등록버튼 활성화
    $('#patient-modify-btn').css('display' , 'none');
    $('#patient-update-btn').css('display' , 'inline-block');
    $('#patient-btn').css('display' , 'none');
    $('#patient-cancel-btn').css('display' , 'inline-block');
}

// 환자 검색
function searchPatient(){

    data = {
        patientId : $('#patientId').val()
    }

    $.ajax({
        url : '/patient-get',
        type: 'POST',
        data: JSON.stringify(data),
        contentType: 'application/json; charset=utf-8',
        success: function (response){
            justInsertPatient(response);
        },
        error: function (xhr, status, error) {
            console.error('AJAX 요청 실패: ', status, error);
        }

    });

}

function justInsertPatient(patientObj){
    // 주소검색 버튼 감추기
    $('#post-search-btn').css('display' , 'none');
    // 환자id 몰래 넣어놓기
    $('#patientId').val(patientObj.patientId);

    // form의 적절한 필드에 넣어주고 readonly로 변경
    $('#patient_name').val(patientObj.patientName).attr('readonly', true);
    $('#identify').val(patientObj.identify).attr('readonly', true);

    // var gender = patientObj.gender? '남' : '여';  // true , false 값이기 때문에 삼항연산자 이용

    $('#gender').val(patientObj.gender.toString()).attr('disabled', true);
    $('#address').val(patientObj.address).attr('readonly', true);
    $('#address-detail').val(patientObj.addressDetail).attr('readonly', true);
    $('#post-code').val(patientObj.postCode).attr('readonly', true);
    $('#age').val(patientObj.age).attr('readonly', true);
    $('#blood-type').val(patientObj.bloodType).attr('readonly', true);
    $('#phone').val(patientObj.phone).attr('readonly', true);
    $('#emergency-phone').val(patientObj.emergencyPhone).attr('readonly', true);

    // 수정 버튼 활성화 | 등록버튼 비활성화
    $('#patient-modify-btn').css('display' , 'inline-block');
    $('#patient-btn').css('display', 'none');
    $('#patient-cancel-btn').css('display' , 'none');
    $('#patient-update-btn').css('display' , 'none');
}

// 진료 접수 팝업창 오픈
function getWaitingPopup(){

    var patientId = $('#patientId').val();

    if($('#patientId').val() === null || $('#patientId').val() === ""){
        alert('환자 정보가 없습니다.');
        return;
    }

    var popup = window.open(
        '/waiting/' + patientId ,
        '진료 접수' ,
        'width=454px , height=565px, top=50, left=50, resizable=no');

}

// 진료 접수 팝업창 수신용 함수
function receiveWaitingInfo(response) {

    var waitingList = JSON.parse(response);  // json형태로 넘겨줬기 때문에 parse
    console.log('waitingList = ' , waitingList);

    insertWaiting(waitingList.content);     // 접수 대기 명단 넣어주기

    waitingPageing(waitingList);
}

// 진료 접수 명단 html 넣기
function insertWaiting(content){

    $('#waiting-list').empty(); // 하위 html 모두 삭제

    console.log('content : ' , content);

    content.forEach(function (waitingUser){

        var html = '<div class="name-card">';

        html += '<div><input type="hidden" value="' + waitingUser.waitingId + '" class="waitingId">';
        html += '<span>' + waitingUser.patientName + '</span><button class="btn" onclick="callPatient(this)">호출</button></div>';
        html += '<div><span>' + waitingUser.identify + '</span>';

        if(waitingUser.age != null && waitingUser.age >= 0){
            html += '<span>' + waitingUser.age + '세</span>';
        }

        html += '<span>' + (waitingUser.gender? '남' : '여') + '</span></div>';

        html += '<div><span>' + waitingUser.waitingDate + '접수</span>';
        html += '<span>' + waitingUser.waitingType + '</span></div>';

        html += '<div><span>' + waitingUser.waitingStatus + '</span></div></div>';

        $('#waiting-list').append(html);
    });
}

// 진료 접수 명단 페이징 html 넣기
function waitingPageing(waitingList){

    $('#waiting-pageing').empty();

    console.log("실행");

    var totalElements = waitingList.totalElements;          // 총 레코드 갯수
    var totalPages = waitingList.totalPages;                // 총 페이지 갯수
    var size = waitingList.size;                            // 보여줄 레코드 수
    var currentPage = waitingList.pageable.pageNumber;      // 현재 페이지

    console.log('totalElements = ' , totalPages , ' totalPages = ' ,
        totalPages, ' size = ' , size , ' currentPage = ' , currentPage);

    $('#waiting-current-page').val(currentPage); // 현재페이지 hidden으로 숨겨놓기

    $('#waiting-user-count').text(totalElements);   // 진료 대기 환자수 변경
    // 진료 완료 환자수 변경 필요

    // var html = '<ul id="waiting-pageing>"';
    var waitingPageing = '';

    var startPage = Math.max(0 , Math.floor(currentPage / 3) * 3);
    var endPage = Math.min(startPage + 3 , totalPages);

    console.log(startPage);
    console.log(endPage);

    if(currentPage >= 3){
        waitingPageing += '<li id="waiting-prev-page" onclick="searchWaiting(' + (startPage - 1) + ')">'+
            '<i class="fa-solid fa-angle-left" style="color: #000000;"></i>' +
            '</li>';
    }

    for(var page = startPage; page < endPage; page++){
        var isActive = (currentPage === page)? 'active' : '';
        waitingPageing += '<li class="' + isActive + '" onclick="searchWaiting(' + page +')">' + (page+1)  + '</li>';
    }

    if(totalPages > endPage){
        waitingPageing += '<li id="waiting-next-page" onclick="searchWaiting(' + endPage + ')">' +
            '<i class="fa-solid fa-angle-right" style="color: #000000;"></i>' +
            '</li>';
    }

    console.log(waitingPageing);
    $('#waiting-pageing').append(waitingPageing);

}

function searchWaiting(page){

    console.log(page);

    var data = {
        page : page
    }

    $.ajax({
       url: "/waiting",
       type: 'post',
       contentType: 'application/json; charset=utf-8',
       data: JSON.stringify(data),
       success: function (response){
           var waitingList = JSON.stringify(response);
           receiveWaitingInfo(waitingList);
       } ,
       error : function (xhr, status, error){
           console.error('AJAX 요청 실패: ', status, error);
       }
    });

}


function callPatient(button){

    var name = $(button).closest('.name-card').find('.name').text();

    var isCall = confirm(name + '님을 호출 할까요?');

    if(isCall === false){
        return ;
    }

    console.log(name + '님을 호출');

}



function checkDouble(input){
    input.value = input.value.replace(/[^0-9.]/g, '').replace(/(\..*)\./g, '$1');
}
function checkInt(input){
    input.value = input.value.replace(/[^0-9]/g, '');
}

function checkIdentify(input){
    var value = input.value.replace(/[^\d]/g, '');  // 숫자만 입력가능
    var addDash = value.replace(/(\d{6})-?(\d{0,7})/, '$1-$2'); // 6자리 이후 - 추가
    input.value = addDash;
}

function checkVital(){
    var flag = true;

    // vital 안에 값 검증, 만약 빈값과 같지 않고 0과 같지 않을 경우 flag를 false로
    $('.vitalValue').each(function (){
        if($(this).val() !== '' && $(this).val() !== '0'){
            flag = false;
            return false; // 하나라도 값이 있는 경우 반복문 빠져나가기
        }
    });
    // flag 값 반환
    return flag;
}

function checkPatient(){
    var flag = false;

    $('.patientValue').each(function () {
        if($(this).val() === '' || $(this).val() === null){
            flag = true;
            return false;
        }
    });

    return flag;
}

// 서브메뉴 contextmenu 이벤트 설정
$(".waiting-type-menu").mouseover(function(){
    $(this).children(".submenu").show();
});
$(".waiting-type-menu").mouseleave(function(){
    $(this).children(".submenu").hide();
});

$(document).on('contextmenu', '.name-card', function(event) {
    event.preventDefault();

    var waitingId = $(this).find('.waitingId').val();

    console.log('waitingId : ' , waitingId);

    $('#custom-context-menu')
        .data(
            'waiting-id' , waitingId
        )
        .css({
        top: event.pageY + 'px',
        left: event.pageX + 'px'
    }).show();
});

$(document).on('click' , function(event){
    if(!$(event.target).closest('#custom-context-menu').length){
        $('#custom-context-menu').hide();
    }
});

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
            alert('성공' + $('#waiting-current-page').val());

            searchWaiting($('#waiting-current-page').val());
        } ,
        error : function (xhr, status, error){
            console.error('AJAX 요청 실패: ', status, error);
        }
    });


});