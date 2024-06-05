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
    window.location.href = 'localhost:8080/logout';
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
}

// 접수 환자 정보 칸 값 넣기 및 readonly와 수정 버튼 활성화
function insertPatient(patientObj){
    // form의 적절한 필드에 넣어주고 readonly로 변경
    $('#patientId').val(patientObj.patientId);

    $('#patient_name').val(patientObj.patientName).attr('readonly', true);
    $('#identify').val(patientObj.identify).attr('readonly', true);

    var gender = patientObj.gender? '남' : '여';  // true , false 값이기 때문에 삼항연산자 이용

    $('#gender').val(gender).attr('readonly', true);
    $('#address').val(patientObj.address).attr('readonly', true);
    $('#address-detail').val(patientObj.addressDetail).attr('readonly', true);
    $('#post-code').val(patientObj.postCode).attr('readonly', true);
    $('#age').val(patientObj.age).attr('readonly', true);
    $('#blood-type').val(patientObj.bloodType).attr('readonly', true);
    $('#phone').val(patientObj.phone).attr('readonly', true);
    $('#emergency-phone').val(patientObj.emergencyPhone).attr('readonly', true);

    $('#patient-modify-btn').css('display' , 'inline-block');
    $('#patient-btn').css('display', 'none');

    searchVitals();

}

// 활력징후 검색
function searchVitals(){

    $('#vital-sign-cancel-btn').css('display' , 'none');
    $('#vital-sign-update-btn').css('display' , 'none');

    var data = {
        patientId : $('#patientId').val()
    };

    $.ajax({
        url: '/vitals-search',
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

    $('#vital-sign-modify-btn').css('display' , 'none');
    $('#vital-sign-add-btn').css('display' , 'none');
    $('#vital-sign-btn').css('display' , 'inline-block');
    $('#vital-sign-cancel-btn').css('display', 'inline-block');
    $('#vital-sign-pageing').hide();

    $('#vitalId').val("");

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

    $('#vital-sign-pageing').css('display','flex');

    var totalElements = response.totalElements; // 총 레코드 갯수
    var totalPages = response.totalPages;       // 총 페이지 갯수
    var size = response.size;               // 보여줄 레코드 수
    var currentPage = response.pageable.pageNumber;      // 현재 페이지
    var patientId = response.content.at(0).patientId;

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


