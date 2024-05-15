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

    $('#patient-modify-btn').show();
    $('#patient-btn').hide();

    var data = {
        patientId : patientObj.patientId
    };

    $.ajax({
        url: '/vitals-search',
        type: 'POST',
        data: JSON.stringify(data),
        contentType: 'application/json; charset=utf-8',
        success: function (response){
            console.log(response);
            insertVitals(response);
        },
        error: function(xhr, status, error) {
            console.error('AJAX 요청 실패: ' , status , error);
        }
    });

}

function insertVitals(response){

    // array 안에 담긴 json 가져오기
    var vitals = response.content.at(0);

    console.log(vitals);

    var vitalDate = vitals.vitalDate.split('T')[0]; // 필요없는 시분초 빼고 날짜만 저장

    var $tr = $('#vital-sign-table tbody tr:nth-child(2)');

    $tr.find('td').eq(0).find('input').val(vitalDate).attr('readonly' , 'true');
    $tr.find('td').eq(1).find('input').val(vitals.weight).attr('readonly' , 'true');
    $tr.find('td').eq(2).find('input').val(vitals.height).attr('readonly' , 'true');
    $tr.find('td').eq(3).find('input').val(vitals.systolicBlood).attr('readonly' , 'true');
    $tr.find('td').eq(4).find('input').val(vitals.diastolicBlood).attr('readonly' , 'true');
    $tr.find('td').eq(6).find('input').val(vitals.pulse).attr('readonly' , 'true');

}


