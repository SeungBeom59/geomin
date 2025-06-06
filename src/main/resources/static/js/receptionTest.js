
//var stompClient = null;         // sockJs 클라이언트
//var connected = false;          // 웹 소켓 연결 여부

let uploadFiles = [];           // 파일 업로드 목록

// 시작시 활력징후 ,  입력표 초기화
$(document).ready(function (){

    var date = new Date();
    var today =  date.getFullYear() + "-" + ("0"+(date.getMonth()+1)).slice(-2) + "-" + ("0"+date.getDate()).slice(-2);
    $('#todayDiagnosisDate').text(today);   // 진료기록 작성창에 오늘 날짜 넣기

    var $tr = $('#vital-sign-table tbody tr:nth-child(2)');
    $tr.find('td').eq(0).find('input').val(today);
    $tr.find('td').eq(1).find('input').val(0);
    $tr.find('td').eq(2).find('input').val(0);
    $tr.find('td').eq(3).find('input').val(0);
    $tr.find('td').eq(4).find('input').val(0);
    $tr.find('td').eq(5).find('input').val(0);
    $tr.find('td').eq(6).find('input').val(0);
});


// 파일 첨부 이벤트 활성화
function onFileEvent(){
    // 첨부파일 박스 html
    const fileDragBox = $('#file-drag-box');

    // 첨부파일 드래그 이벤트 추가
    fileDragBox.on('dragenter' , highlight );
    fileDragBox.on('dragover' , highlight );
    fileDragBox.on('dragleave' , unhighlight );
    fileDragBox.on('drop' , handleDrop );

    $('#file-upload').on('change', fileSelect);
}
// 파일 첨부 이벤트 비활성화
function offFileEvent(){
    // 첨부파일 박스 html
    const fileDragBox = $('#file-drag-box');

    // 첨부파일 드래그 이벤트 추가
    fileDragBox.off('dragenter' , highlight );
    fileDragBox.off('dragover' , highlight );
    fileDragBox.off('dragleave' , unhighlight );
    fileDragBox.off('drop' , handleDrop );

    $('#file-upload').off('change', fileSelect);
}

function fileSelect(e){
    $('#file-drag-box > i').hide();
    $('#file-drag-box > p').hide();

    let files = e.target.files;
    console.log('선택된 파일' , files);

    processFile(files);
}


// 첨부파일 드랍박스 하이라이트
function highlight(e){
    const fileDragBox = $('#file-drag-box');

    console.log('drag enter or over');
    e.preventDefault();     // 이벤트 방지
    fileDragBox.addClass("highlight");
}

// 하이라이트 제거
function unhighlight(e){
    const fileDragBox = $('#file-drag-box');
    console.log('drag leave');
    e.preventDefault();
    fileDragBox.removeClass("highlight");
}


function handleDrop(e){

    $('#file-drag-box > i ').hide();
    $('#file-drag-box > p').hide();

    console.log('file dropped');
    unhighlight(e);
    e.preventDefault();

    var files = e.originalEvent.dataTransfer.files;     // 파일 정보를 담은 유사배열

    processFile(files);
}

function processFile(files){

    const fileList = $('#files');   // 파일 담을 div dom

    console.log('files' , files);

    var fileArray = [...files];     // 위의 유사배열인 files의 내용을 복사하여 진짜 배열로 변경.

    // 파일 업로드 목록 담기 처리 및 파일 목록 출력
    for(var i=0; i < fileArray.length; i++){

        // 중복파일 무효화 처리
        // 업로드파일 목록에 있는 파일을 순차로 돌면서 파일이름이 fileArray 이번 회차 파일이름과 같은지 확인.
        if(uploadFiles.some(f => f.name === fileArray[i].name && f.size === fileArray[i].size)){
            console.log('중복 파일 제외 : ' , fileArray[i].name);
            continue;   // 루프 건너뜀.
        }

        fileList.append(renderFile(fileArray[i]));  // div에 만들어진 파일 html 추가
        uploadFiles.push(fileArray[i]);             // 업로드할 목록에 담아준다.
    }

    console.log('uploadFiles[] ' , uploadFiles);
    // return uploadFiles;
}


// 파일 사이즈 출력
function formatFileSize(sizeInBytes) {
    if (sizeInBytes < 1024) {
        return `${sizeInBytes} B`; // 바이트 단위로 표시
    } else if (sizeInBytes < (1024*1024)) {
        return `${(sizeInBytes / 1024).toFixed(1)} kb`; // KB 단위로 표시
    } else {
        return `${(sizeInBytes / 1048576).toFixed(1)} mb`; // MB 단위로 표시
    }
}

// (업로드 되지 않은)파일 삭제
function deleteFile(clickedBox){

    var fileBox = $(clickedBox).closest('.file');
    var fileName = fileBox.find('.name').text();
    var fileSize = fileBox.find('.size').text();

    //  일치하지 않는 파일(업로드예정파일)들만 다시 uploadFiles에 넣어주면 삭제예정파일은 빠지게 된다.
    uploadFiles = uploadFiles.filter(function (file){
        return !(file.name === fileName && formatFileSize(file.size) === fileSize);
    });

    fileBox.remove();

    console.log('delete after uploadFiles' , uploadFiles);
}

// 파일 html 변환
function renderFile(file) {
    var fileHtml = $('<div></div>');
    fileHtml.addClass('file');

    fileHtml.append(`
            <div class="thumbnail">
                
            </div>
            
            <div class="file-info">
                <span class="name">${file.name}</span>
                <span class="size">${formatFileSize(file.size)}</span>
            </div>
            
            <div onclick="deleteFile(this)">
                <span>X</span>
            </div>
        `)

    // <img src="" title="${escape(file.name)}">
    // 파일 미리보기 처리 (이미지 파일만 해당)
    if (file.type.startsWith('image/')) {
        var reader = new FileReader();
        reader.onload = function(e) {
            // fileHtml.find('img').attr('src', e.target.result);
            fileHtml.find('.thumbnail').html(`<img src="${e.target.result}" title="${escape(file.name)}" />`);
        };
        reader.readAsDataURL(file);
    }
    // image 파일이 아닌 다른 유형일 경우 아래의 아이콘 삽입하도록
    else {

        var iconType = '';

        if(file.type === 'application/pdf'){
            iconType = 'fa-file-pdf';
        }
        else if(file.type.startsWith('text/')){
            iconType = 'fa-file-lines';
        }
        else if(file.type.startsWith('video/')){
            iconType = 'fa-file-video';
        }
        else if(file.type.startsWith('audio')){
            iconType = 'fa-file-audio';
        }
        else {
            iconType = 'fa-file';
        }
        fileHtml.find('.thumbnail').html(`<i class="fa-regular ${iconType}"></i>`)
    }

    return fileHtml;
}

// var btn = $('#diagnosis-create-btn');
// btn.on('click' , updateDiagnosis);

// 진료기록 삭제 진행.
function deleteDiagnosis(button){

    var diagnosisId;
    // 버튼에 할당된 data 값에 따라서 (new? past?)
    // 가져올 hidden input value를 다르게 하고 ajax
    var dataDiagnosisId = $(button).data('diagnosis-id');

    if(dataDiagnosisId === 'new'){
        diagnosisId = $('#new-diagnosisId').val();
    }
    else if(dataDiagnosisId === 'past'){
        diagnosisId = $('#past-diagnosis-id').val();
    }

    if (!diagnosisId) {
        alert('유효한 진료기록 ID가 없습니다.');
        return;
    }

    $.ajax({
        url: "/diagnosis-delete/" + diagnosisId,
        type: 'post',
        contentType: 'application/json; charset=UTF-8',
        success: function (response){
            alert('성공');
            clearDiagnosis();
            getDiagnosisList();
        },
        error: function (xhr, status, error){
            console.log('ajax 실패' , status , error);
        }
    })
}


// 과거 진료기록에서 우측 진료기록 작성칸으로 수정 진행.
function changeDiagnosis(button){

    let pastDiagnosisId = $('#past-diagnosis-id').val();
    var dataDiagnosisId = $(button).data('diagnosis-id');

    if(dataDiagnosisId === 'read'){

        var diagnosisId = pastDiagnosisId;

        $.ajax({
            url: "/diagnosis/" + diagnosisId,
            type: 'post',
            contentType: 'application/json; charset=UTF-8',
            success: function (response){
                readDiagnosis(response);
            },
            error : function (xhr , status, error) {
                console.log('ajax 실패' , status , error);
            }
        })

    }
    else{
        $('#new-diagnosisId').val(pastDiagnosisId);
        callDiagnosis();
    }
}

// 수정 또는 취소 버튼을 누를 경우
// 수정 : 진료기록 정보를 가져와 수정가능케함.
// 취소 : 진료기록 정보를 가져와 조회만 가능토록 함.
function callDiagnosis(button){
    console.log('callDiagnosis 실행');

    var buttonId = button? button.id : null;
    console.log('buttonId' , buttonId);

    let diagnosisId = $('#new-diagnosisId').val();

    $.ajax({
        url: "/diagnosis/" + diagnosisId,
        type: 'post',
        contentType: 'application/json; charset=UTF-8',
        success: function (response){
            alert('성공');
            if(buttonId === "diagnosis-cancel-btn"){
                alert('취소 처리.');
                readDiagnosis(response);
            }
            else {
                console.log('else로 빠짐.');
                insertDiagnosis(response);
            }
        },
        error : function (xhr , status, error) {
            console.log('ajax 실패' , status , error);
        }
    })
}

// 진료기록 작성|수정
function updateDiagnosis(){

    // console.log('updateDiagnosis' ,  $('#diagnosis-yn').val());


    var symptomsValue = $('#symptoms-record').val().trim();
    var diagnosisValue = $('#diagnosis-record').val().trim();

    if(!symptomsValue || !diagnosisValue){
        alert('증상 및 진료 기록은 필수입니다. ');
        return;
    }

    // diagnosisId가 null 또는 "" 이라면 새로작성, 아니라면 수정
    // 진료접수를 받으면 diagnosisId가 무조건 생기는데 왜 없는 경우를 가정하느냐는
    // 진료접수 없이 새로운 진료기록을 작성하는 경우를 고려함.
    var $diagnosisId = $('#new-diagnosisId');
    var diagnosisId = $diagnosisId.val() === null || $diagnosisId.val() === ""?
        0 : $diagnosisId.val();

    // modifier도 위와 같음.
    var $diagnosisModifier = $('diagnosisModifier');
    var diagnosisModifier = $diagnosisModifier.val() === null || $diagnosisModifier === ""?
        null : $diagnosisModifier.val();

    // 첨부 자료들에 대한 Id가 있는 경우, 해당 Id에 업로드 및 수정처리를 해줘야 함.
    // 없을 경우 0으로 값 보냄.
    var fileId = $('#fileId').val() === '' || $('#fileId').val() === null ? 0 : $('#fileId').val();
    var kcdId = $('#kcdId').val() === '' || $('#kcdId').val() === null ? 0 : $('#kcdId').val();
    var medicineId = $('#pillId').val() === '' || $('#pillId').val() === null ? 0 : $('#pillId').val();
    var treatmentId = $('#treatmentId').val() === '' || $('#treatmentId').val() === null ? 0 : $('#treatmentId').val();
    var medicalBillId = $('#medicalBillId').val() === '' || $('#medicalBillId').val() === null ? 0 : $('#medicalBillId').val();


    // ajax 데이터
    var data = new FormData();

    data.append("diagnosis" , new Blob([JSON.stringify({
        patientId : $('#new-patientId').val(),
        diagnosisId : diagnosisId,
        symptoms : $('#symptoms-record').val().replace('\r\n' , '<br\>'),
        diagnosis : $('#diagnosis-record').val().replace('\r\n' , '<br\>'),
        prescription : $('#prescription-record').val().replace('\r\n' , '<br\>'),
        diagnosisModifier : diagnosisModifier,
        diagnosisYn : $('#diagnosisYn').val(),
        fileId : fileId,
        kcdId : kcdId,
        medicineId : medicineId,
        treatmentId : treatmentId,
        medicalBillId : medicalBillId
    })] , { type: "application/json; charset=utf-8"}));

    // 의약품 처방 데이터 가져오기
    var pills = collectPillsData();
    if(pills.length > 0){
        data.append("pills" ,
            new Blob([JSON.stringify(pills)], {type : "application/json; charset=utf-8"}));
    }

    // 업로드 파일 데이터 가져오기
    uploadFiles.forEach(function (file){
        data.append("uploadFiles" , file);
    });

    // 삭제예정 파일들 데이터 가져오기
    var deleteFiles = [];
    $('.deleteFile').each(function() {
        deleteFiles.push($(this).val());
    });

    if(deleteFiles.length > 0){
        data.append("deleteFiles",
            new Blob([JSON.stringify(deleteFiles)] , {type : "application/json; charset=utf-8"}));
    }

    // 질병기록 kcd 데이터 가져오기
    sortKcds();
    var kcds = [];
    $('#sortable-container .kcd').each(function () {

        var kcdData = {
            kcdCode : $(this).find('li:nth-child(1)').text(),
            kcdName : $(this).find('li:nth-child(2)').text(),
            kcdType : $(this).find('li:nth-child(3) > select').val(),
            kcdRank : $(this).attr('data-rank')
        }
        kcds.push(kcdData);
    });
    if(kcds.length > 0){
        data.append("kcds", new Blob([JSON.stringify(kcds)] , {type : "application/json; charset=utf-8"}));
    }
    // 삭제예정 질병기록코드 데이터 가져오기
    // var deleteKcds = [];
    // $('.deleteKcd').each(function (){deleteKcds.push($(this).val()); });
    // if(deleteKcds.length > 0){
    //     data.append("deleteKcds",
    //         new Blob([JSON.stringify(deleteKcds)] , {type : "application/json; charset=utf-8"}));
    // }

    // 처방기록 가져오기
    var treatments = collectTreatment();
    var medicals = collectMedical();

    if(treatments.length > 0){
        data.append("treatments" , new Blob([JSON.stringify(treatments)] , {type : "application/json; charset=utf-8"}));
    }

    if(medicals.length > 0){
        data.append("medicals" , new Blob([JSON.stringify(medicals)] , {type : "application/json; charset=utf-8"}));
    }


    console.log('pills : ' , pills);
    console.log("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    console.log("제대로 medicalBillId 넘어가고 있는거냐!");
    console.log("medicalBillId" , medicalBillId);
    console.log('data : ' , data);
    console.log('deleteFiles : ' , deleteFiles);
    console.log('medicals : ' , medicals);
    console.log('treatments : ' , treatments);
    // console.log('deleteKcds' , deleteKcds);

    $.ajax({
        url: '/diagnosis-update',
        type: 'post',
        processData: false,
        contentType: false,
        data : data,
        success: function (response){
            alert('성공');
            console.log('updateResponse : ' , response);
            uploadFiles = [];   // 업로드 파일 리스트 초기화
            // offFileEvent(); // 파일 이벤트 비활성화
            readDiagnosis(response);
            searchWaiting($('#waiting-current-page').val());
            getDiagnosisList();
        },
        error: function(xhr, status, error) {
            console.error('AJAX 요청 실패: ' , status , error);
        }
    })
}

function sortKcds(){
    $('#sortable-container .kcd').each(function (index){
       $(this).attr('data-rank' , index + 1);
    });
}

// 완전히 새로운 진료기록 작성, (db 일관성 문제로 삭제)
//function newDiagnosis(){
//
//    var patientId = $('#patientId').val();
//    var patientName = $('#patient_name').val();
//
//    if(patientId === null || patientId === ''){
//        alert('환자 정보를 먼저 설정 해주세요.');
//        return;
//    }
//
//    clearDiagnosis();   // 진료기록 작성 div 초기화
//
//    $('#diagnosis-modify-btn').hide();
//    $('#diagnosis-create-btn').show();
//    $('#symptoms-record').attr('readonly' , false);
//    $('#diagnosis-record').attr('readonly' , false);
//    $('#prescription-record').attr('readonly' , false);
//
//    onFileEvent();  // 파일 이벤트 활성화
//
//    $('#new-patientId').val(patientId);
//
//    $('#diagnosis-write-title > span').show();              // 모든 span 보여주기(날짜:시간, 환자명)
//    $('#diagnosis-write-title > span:nth-child(2)').show();
//    $('#diagnosis-write-title > span:nth-child(3)').text(patientName);   // 환자명 넣기
//
//    $('#sortable-container').sortable({item: 'ul.kcd'}); // .kcds div안에 .kcd를 갖는 ul 정렬 기능 활성화.
//}


// 처방한 의약품 json 형태로 만들어서 data에 넣기
function collectPillsData() {
    var pillsData = [];

    $('#pills .pill').each(function () {
        var pillData = {
            itemSeq: $(this).find('.itemSeq').val(),                         // hidden의 value
            medicineName: $(this).find('li').eq(0).text(),                       // 첫 번째 li의 텍스트 (약명칭)
            dosage: $(this).find('li').eq(1).find('input').val(),     // 두 번째 li의 input value (용량)
            frequency: $(this).find('li').eq(2).find('input').val(),  // 세 번째 li의 input value (빈도)
            days: $(this).find('li').eq(3).find('input').val()        // 네 번째 li의 input value (복용 일수)
        };

        pillsData.push(pillData);
    });

    console.log('collectPillsData()\'s pillsData ' , pillsData);

    return pillsData;
}




// todo : 공통 js , 공통 css 분리하기
// todo: reception에서는 이렇게 받도록 처리하고 다른 페이지에서는 reception으로 넘기도록 처리
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

    $('#patient_name').focus();
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
    getDiagnosisList();


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
                $('#vital-sign-cancel-btn').css('display', 'none');
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

    vitalspaging(response);

}

// 활력징후 값 초기화
function clearVitals(){

    // 버튼 보이기 설정
    $('#vital-sign-modify-btn').css('display' , 'none');
    $('#vital-sign-add-btn').css('display' , 'none');
    $('#vital-sign-btn').css('display' , 'inline-block');
    $('#vital-sign-cancel-btn').css('display', 'inline-block');
    $('#vital-sign-paging').hide();

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
function vitalspaging(response){

    if (!response || !response.pageable || response.totalElements <= 1) {
        $('#vital-sign-paging').hide();
        return;
    }

    console.log("페이징 response : ");
    console.log(response);

    $('#vital-sign-paging').css('display','flex'); // 페이징 보여주기

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

    $('#vital-sign-paging li').not('#prev-page, #next-page').remove(); // 기존 페이지 번호 삭제

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
                $('#vital-sign-paging').hide();
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
                $('#vital-sign-paging').hide();
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
                $('#vital-sign-paging').hide();
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

// todo: 다른 페이지에서 진료접수 클릭하면 reception으로 이동하도록 해야함
// todo: reception에서는 아래 함수 사용
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








// todo: reception에서는 아래 함수 사용
// todo: 다른 페이지에서는 reception으로 이동하도록 처리
// 진료대기 환자 호출 함수 waitingList patient call
function callPatient(button){

    var name = $(button).closest('.name-card').find('.name').text();
    var id = $(button).closest('.name-card').find('.waitingId').val();

    var departmentId = $('#departmentId').val();

    var isCall = confirm(name + '님을 호출 할까요?');

    if(isCall === false){
        return ;
    }

    console.log(name + '님을 호출');
    console.log('id : ' + id);

    var data = {
        waitingId: id,
        departmentId: departmentId
    }

    $.ajax({
        url:'/patient-call',
        type: 'post',
        contentType: 'application/json; charset=utf-8',
        data: JSON.stringify(data),
        success: function (response){
            alert('성공');
            console.log(response);
            var patientObj = response.patientDTO;
            var todayDiagnosis = response.diagnosisDTO;
            console.log(patientObj);
            console.log(todayDiagnosis);

            searchWaiting($('#waiting-current-page').val());    // waitingList 새로고침
            insertPatient(patientObj);
            insertDiagnosis(response);

            localStorage.removeItem('selectedPills');   // 로컬스토리지에 의약품 정보 삭제
        },
        error: function (xhr, status, error){
            console.error('AJAX 요청 실패: ' , status, error);
        }
    })
}

function checkDashAndNumbers(input) {
    input.value = input.value.replace(/[^0-9-]/g, '');

    // 하이픈이 입력값의 첫 번째 문자로 오지 않도록 처리
    if (input.value.startsWith('-')) {
        input.value = '-' + input.value.replace(/^-/, '');
    }
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



// 접수대기환자에서 호출 후, 진료중 버튼 누를 경우.
function endPatient() {

}

// 진료기록 order 로 가져와주기
function getDiagnosisOrder(){

    var patientId = $('#patientId').val();
    var departmentId = $('#departmentId').val();
    var order = $('#past-record-order').val();
    var page= 0;

    if(patientId === null || patientId === ""){
        alert('환자를 먼저 선택해주세요.');
        return;
    }

    var data = {
        patientId : patientId,
        departmentId : departmentId,
    }

    $.ajax({
        url:"/diagnosis?page=" + page + "&sort=" + order,
        type: 'post',
        contentType: 'application/json; charset=utf-8',
        data: JSON.stringify(data),
        success: function (response){
            alert('성공');
            console.log('getDiagnosisList : ' , response );
            if(response.diagnosisDTOList.length > 0 ){
                insertPastDiagnosis(response);
                pastDiagnosispaging(response);
            }
            else if(response.diagnosisDTO !== null){
                insertPastDiagnosis(response);
            }
            else {
                clearPreDiagnosisList();
            }

        },
        error: function (xhr, status, error){
            console.error('AJAX 요청 처리 실패 : ', status, error);
            clearPreDiagnosisList();
        }
    });
}

// 진료기록 가져오기
function getDiagnosisList(){

    var patientId = $('#patientId').val();
    var departmentId = $('#departmentId').val();
    var order = $('#past-record-order').val();
    var page= $('#past-record-page').val() === null || $('#past-record-page').val() === ""? 0 : $('#past-record-page').val();

    var data = {
        patientId : patientId,
        departmentId : departmentId,
    }

    $.ajax({
        url:"/diagnosis?page=" + page + "&sort=" + order,
        type: 'post',
        contentType: 'application/json; charset=utf-8',
        data: JSON.stringify(data),
        success: function (response){
            alert('성공');
            console.log('getDiagnosisList : ' , response );
            if(response.diagnosisDTOList.length > 0 ){
                insertPastDiagnosis(response);
                pastDiagnosispaging(response);
            }
            else if(response.diagnosisDTO !== null){
                insertPastDiagnosis(response);
            }
            else {
                clearPreDiagnosisList();
            }

        },
        error: function (xhr, status, error){
            console.error('AJAX 요청 처리 실패 : ', status, error);
            clearPreDiagnosisList();
        }
    });
}


// 과거 진료기록 리스트 : 검색결과 있음 (넣어주기)
function insertPastDiagnosis(response){

    var content = response.diagnosisDTOList !== null? response.diagnosisDTOList.at(0) : response.diagnosisDTO;
    var symptoms = content.symptoms;
    var diagnosis = content.diagnosis;
    var prescription = content.prescription;


    console.log('insertpast : ' , response);
    var attendingDoctor = content.doctorName;
    var date = content.diagnosisDate;

    if(content.modifierName !== null){
        var modifier = content.modifierName;
        var modifyDate = content.modifyDate;

        $('#past-modify-box > span:first-child').text(modifyDate);
        $('#past-modify-box > span:nth-child(3)').text(modifier);

        $('#past-modify-box').show();
    }
    else {
        $('#past-modify-box').hide();
    }

    $('#past-diagnosis-id').val(content.diagnosisId);                   // diagnosisId 넣기
    $('#diagnosis-record-list').show();
    $('.pre-diagnosis-box').show();                                     // 진료기록 박스 활성화
    $('#no-record-diagnosis').hide();                                   // 진료기록 없다는 메세지 숨김
    $('#diagnosis-title span').show();                                  // 진료기록 타이틀 옆 날짜 표시
    $('.diagnosis-modify-btn').show();                                  // 수정버튼 활성화
    $('.diagnosis-delete-btn').show();                                  // 삭제버튼 활성화
    $('#past-to-read').show();                                          // 우측 창에서 조회 버튼 활성화
    $('#pre-files-box').show();     // 추가 정보 박스 활성화
    $('#attending-doctor-box').css('display' , 'flex');     // 담당의 박스 활성화
    $('.pre-pay-box').show();

    var name = content.patientName;
    $('#diagnosis-title span:last-child').text(name);       // 진료기록 환자 성명
    $('#attending-doctor-box span:last-child').text(attendingDoctor);   // 담당의
    $('#diagnosis-title span:first-child').text(date);      // 진료날짜

    $('#symptoms-past-record').val(symptoms);               // 증상 textarea
    $('#diagnosis-past-record').val(diagnosis);             // 진료 textarea
    $('#prescription-past-record').val(prescription);       // 처방 textarea

    var fileInfo = Array.isArray(response.fileInfoDTOList)? response.fileInfoDTOList : [];
    var medicine = Array.isArray(response.medicineDTOList)? response.medicineDTOList : [];
    var kcds = Array.isArray(response.kcdDTOList)? response.kcdDTOList : [];

    var treatments = Array.isArray(response.treatmentDTOList)? response.treatmentDTOList : [];
    var medicals = Array.isArray(response.medicalMaterialDTOList)? response.medicalMaterialDTOList : [];

    ////////////////// 질병정보
    var kcdBox = $('#pre-kcds');
    kcdBox.find('.pre-kcd').remove();

    if(kcds.length > 0){
        $('#no-pre-kcds').hide();

        kcds.sort(function (a , b) {
           return a.kcdRank - b.kcdRank;
        });

        kcds.forEach(function (kcd){

            var kcdTypeOption = whatKcdType(kcd.kcdType);
            // kcdTypeOption += '<option value="1"' + (kcd.kcdType === 1? 'selected' : '') + '>주상병</option>';
            // kcdTypeOption += '<option value="2"' + (kcd.kcdType === 2? 'selected' : '') + '>기타상병</option>';
            // kcdTypeOption += '<option value="3"' + (kcd.kcdType === 3? 'selected' : '') + '>배제된상병</option>';

            kcdBox.append(
                '<ul class="pre-kcd" data-rank="' + kcd.kcdRank +'">' +
                '<li>' + kcd.kcdCode + '</li>' +
                '<li>' + kcd.kcdName + '</li>' +
                '<li>' + kcdTypeOption+ '</li>' +
                '</ul>'
            );
        });
    }
    else {
        $('#no-pre-kcds').css('display' , 'flex');
    }


    ////////////////// 의약품
    var pillsBox = $('#pre-pills');
    pillsBox.find('.pre-pill').remove();

    if(medicine.length > 0){
        $('#no-pre-pills').hide();
        medicine.forEach(function (pill){

            pillsBox.append(
                '<ul class="pre-pill">' +
                    '<input type="hidden" class="itemSeq" value="' + pill.itemSeq + '">' +
                    '<li>' + pill.medicineName +'</li>' +
                    // '<li><input type="text" value="' + pill.dosage + '" autocomplete="off" oninput="checkDouble(this)" readonly></li>' +
                    // '<li><input type="text" value="' + pill.frequency + '" autocomplete="off" oninput="checkDouble(this)" readonly></li>' +
                    // '<li><input type="text" value="' + pill.days + '" autocomplete="off" oninput="checkDouble(this)" readonly></li>' +
                    '<li>' + pill.dosage + '</li>' +
                    '<li>' + pill.frequency + '</li>' +
                    '<li>' + pill.days + '</li>' +
                '</ul>'
            );
        });
    }
    else {
        $('#no-pre-pills').css('display' , 'flex');
    }

//    ////////// 처방수가 & 치료재료
    var pastPayBox = $('#past-pay');
    pastPayBox.find('.past-treatment-data').remove();
    pastPayBox.find('.past-medical-data').remove();

    if(treatments.length > 0 || medicals.length > 0){
        $('#no-past-pay').hide();
    }
    else{
        $('#no-past-pay').css('display' , 'flex');
        $('#past-treatment-box').hide();
        $('#past-medical-box').hide();
    }

    if(treatments.length > 0){
        var pastTreatmentsBox = $('#past-treatment-box');
        pastTreatmentsBox.show();

        treatments.forEach(function(treatment){

            var benefitType = treatment.benefitType == true? '급여' : '비급여';
            var benefitClass = treatment.benefitType == true? 'benefit' : 'non-benefit';
            var surgeryYn = treatment.surgeryYn == true? '수술' : '비수술';
            var surgeryClass = treatment.surgeryYn == true? 'surgery' : 'no-surgery';

            var treatmentHtml =
                '<li class="past-treatment-data"><div>' +
                '<span>' + treatment.feeCode + '</span>' +
                '<span>' + treatment.codeName + '</span></div>' +
                '<div><span class="' + benefitClass + '">' + benefitType + '</span>' +
                '<span class="' + surgeryClass + '">' + surgeryYn + '</span></div></li>';

            pastTreatmentsBox.append(treatmentHtml);
        });
    }

    if(medicals.length > 0){
        var pastMedicalBox = $('#past-medical-box')
        pastMedicalBox.show();

        medicals.forEach(function(medical){

            var medicalHtml =
                '<li class="past-medical-data">' +
                '<span>' + medical.mmCode + '</span>' +
                '<span>' + medical.mmName + '</span></li>';

            pastMedicalBox.append(medicalHtml);
        });
    }

    ///////////////// 파일
    var filesBox = $('#pre-files');
    filesBox.find('.pre-file').remove();

    if(fileInfo.length > 0){
        $('#no-pre-files').hide();

        fileInfo.forEach(function (file){
            var fileHtml = readPastFileRender(file);
            filesBox.append(fileHtml);
        });
    }
    else {
        $('#no-pre-files').css('display' , 'flex');
    }



}

function whatKcdType(kcdType){

    var kcdTypeOption = '';

    switch (kcdType){
        case 1:
            kcdTypeOption = '주상';
            break;
        case 2:
            kcdTypeOption = '기타';
            break;
        case 3 :
            kcdTypeOption = '배제';
            break;
        default :
            kcdTypeOption = '미정';
    }
    return kcdTypeOption;
}

function readPastFileRender(file){

    var fileHtml = $('<ul></ul>');
    fileHtml.addClass('pre-file');

    if(file.img === true){
        fileHtml.append(`
            <li>
                <img src="/files/${file.link}" alt="${file.orgFileName}" onclick="viewImage('${file.saveFileName}')"/>
            </li>
        `);
    }
    else {
        fileHtml.append(`
            <li>
                <a onclick="viewImage('${file.saveFileName}')">
                    <i class="fa-regular fa-file"></i>
                </a>
            </li>
        `);
    }

    var saveFileName = encodeURIComponent(file.saveFileName);

    fileHtml.append(`
        <li>
            <span>${file.orgFileName}</span>
            <span>${formatFileSize(file.fileSize)}</span>
        </li>
        <li>
            <a href="/files/download/${saveFileName}" download="${file.orgFileName}">
                <i class="fa-solid fa-download"></i>
            </a>
        </li>   
    `);

    return fileHtml;
}


// // 진료기록 리스트 : 검색결과 있음 (넣어주기)
// function insertPastDiagnosis(response){
//
//     var content = response.content.at(0);
//     var symptoms = content.symptoms;
//     var diagnosis = content.diagnosis;
//     var prescription = content.prescription;
//
//     console.log('insertpast : ' , response);
//     // var attendingDoctor = content.diagnosisModifier !== null?
//     //     content.diagnosisModifier : content.doctorName;
//
//     var attendingDoctor = content.doctorName;
//
//     var date = content.modifyDate !== null?
//         content.modifyDate : content.diagnosisDate;
//
//     $('#past-diagnosis-id').val(content.diagnosisId);                   // diagnosisId 넣기
//
//     $('.pre-diagnosis-box').show();                                     // 진료기록 박스 활성화
//     $('#no-record-diagnosis').hide();                                   // 진료기록 없다는 메세지 숨김
//     $('#diagnosis-title span').show();                                  // 진료기록 타이틀 옆 날짜 표시
//     $('.diagnosis-modify-btn').show();                                  // 수정버튼 활성화
//     $('#pre-files-box').show();     // 추가 정보 박스 활성화
//     $('#attending-doctor-box').css('display' , 'flex');     // 담당의 박스 활성화
//
//     // if문으로 사진이 있을 경우에만.
//     // $('#diagnosis-photo-box').css('display' , 'flex');      // 진료기록 사진 박스 활성화
//
//     var name = $('#patient_name').val();
//     $('#diagnosis-title span:last-child').text(name);       // 진료기록 환자 성명
//     $('#attending-doctor-box span:last-child').text(attendingDoctor);   // 담당의
//     $('#diagnosis-title span:first-child').text(date);      // 진료날짜
//
//     $('#symptoms-past-record').val(symptoms);               // 증상 textarea
//     $('#diagnosis-past-record').val(diagnosis);             // 진료 textarea
//     $('#prescription-past-record').val(prescription);       // 처방 textarea
// }


// 진료기록 리스트 : 검색결과 없음 표시
function clearPreDiagnosisList(){

    $('.pre-diagnosis-box').hide();
    $('#no-record-diagnosis').css('display' , 'flex');
    $('#diagnosis-title span').show();
    $('#diagnosis-title span:not(:last-child)').hide();
    $('.diagnosis-modify-btn').hide();
    $('.diagnosis-delete-btn').hide();
    $('#past-to-read').hide();
    $('#past-modify-box').hide();
    $('#pre-files-box').hide();
    $('.pre-file').remove();
    $('.pre-pay-box').hide();

    var name = $('#patient_name').val();
    $('#diagnosis-title span:last-child').text(name);

    $('#past-diagnosis-id').val("");                // 과거진료기록 번호 삭제

    // 페이징도 지워줘야함.
    $('#diagnosis-record-list').hide();
    $('#past-record-page').val("");
    $('#diagnosis-record-list').find('.date').remove();
    $('#diagnosis-record-paging').empty();
}



function pastDiagnosispaging(response){

    $('#past-record-page').val("");

    let paging = $('#diagnosis-record-paging');
    paging.empty();

    let listHtml = $('#diagnosis-record-list');
    listHtml.find('.date').remove();

    var list = Array.isArray(response.diagnosisDTOList)? response.diagnosisDTOList : [];

    list.forEach(function (diagnosis){
//        console.log('diagnosis' , diagnosis);

        let date = diagnosis.diagnosisDate.substring(0, diagnosis.diagnosisDate.indexOf('/'));

        listHtml.append(
            '<li class="date" onclick="getDiagnosis('+ diagnosis.diagnosisId + ')">' + date + '</li>'
        )
    });

    let currentGroup = response.currentGroup;
    let currentGroupEndPage = response.currentGroupEndPage;
    let currentGroupStartPage = response.currentGroupStartPage;
    let nextGroupStartPage = response.nextGroupStartPage;
    let page= response.page;
    let prevGroupStartPage = response.prevGroupStartPage;
    let size = response.size;
    let totalCount = response.totalCount;
    let totalPages = response.totalPages;

    if(page > 0){
        paging.append(`
            <li><i class="fa-solid fa-angles-left" onclick="changePage(0)"></i></li> 
            <li><i class="fa-solid fa-angle-left" onclick="changePage(${page}-1)"></i></li>
        `)
    }

    if(page < (totalPages-1) ){
        paging.append(`
            <li><i class="fa-solid fa-angle-right" onclick="changePage(${page}+1)"></i></li>
            <li><i class="fa-solid fa-angles-right" onclick="changePage((${totalPages}-1))"></i></li
        `)
    }
}

function changePage(value){

    $('#past-record-page').val(value);
    getDiagnosisList();

}

function getDiagnosis(diagnosisId){

    $.ajax({
        url: "/diagnosis/" + diagnosisId,
        type: 'post',
        contentType : 'application/json; charset=UTF-8',
        success: function (response){
            insertPastDiagnosis(response);
        },
        error: function (xhr , status, error){
            console.error('AJAX 요청 처리 실패 : ', status, error);
        }
    })

}

// 진료기록 작성칸 활성화 해주기 | 수정
function insertDiagnosis(response){

    alert('insertDiagnosis() 실행');

    clearDiagnosis();

    let diagnosis = response.diagnosisDTO;
    var fileInfo = Array.isArray(response.fileInfoDTOList)? response.fileInfoDTOList : [];
    var medicine = Array.isArray(response.medicineDTOList)? response.medicineDTOList : [];
    var kcds = Array.isArray(response.kcdDTOList)? response.kcdDTOList : [];
    var treatments = Array.isArray(response.treatmentDTOList)? response.treatmentDTOList : [];
    var medicalMaterials = Array.isArray(response.medicalMaterialDTOList)? response.medicalMaterialDTOList : [];

    console.log('response' , response);
    console.log('fileInfo' , fileInfo);
    console.log('insertDiagnosis : ' , diagnosis);

    onFileEvent();  // 파일 이벤트 활성화

    if(diagnosis != null && diagnosis.diagnosisYn === true){

        // true 라면 진료했던 기록이므로 버튼 설정 변경
        $('#diagnosis-cancel-btn').show();  // 취소 버튼 활성화
        $('#diagnosis-modify-btn').hide();  // 수정 버튼 비활성화

        $('#diagnosisYn').val(true);

        var diagnosisRecord = diagnosis.diagnosis.replace('\r\n' , '<br\>');
        var prescriptionRecord = diagnosis.prescription.replace('\r\n' , '<br\>');
        var symptomsRecord = diagnosis.symptoms.replace('\r\n' , '<br\>');
        $('#symptoms-record').val(symptomsRecord);     // 증상 값 넣기
        $('#diagnosis-record').val(diagnosisRecord);          // 진료 기록 넣기
        $('#prescription-record').val(prescriptionRecord);    // 처방 기록 넣기

        // 파일처리
        $('#file-add').show();   // 파일등록으로 제목변경
        $('#file-read').hide();  ////////////////////
        $('.file-label').show(); // 내PC 라벨 활성화
        $('#no-files').hide();  // 저장된 파일 없음 설명글 비활성화

        if(fileInfo !== null && fileInfo.length > 0 ){

            $('#file-drag-box > i').hide();
            $('#file-drag-box > p').hide();

            fileInfo.forEach(function (file){
                let fileHtml = renderFileForUpdate(file);
                $('#files').append(fileHtml);
            });
            $('#fileId').val(fileInfo.at(0).fileId);
            console.log($('#fileId').val(fileInfo.at(0).fileId));
        }
        else{

            $('#file-drag-box > i').show();
            $('#file-drag-box > p:nth-child(2)').show();
        }

        if(medicine.length > 0){
            $('#no-pills').hide();
            $('#pillId').val(medicine.at(0).medicineId);

            medicine.forEach(function (pill){
               renderPills(pill);
            });
            uploadLocalStorage()  // 추가된 약들을 localStorage에 업로드 해줄것.
        }
        else {
            $('#no-pills').css('display' , 'flex');
        }

        if(kcds.length > 0){
            $('#no-kcd').hide();

            $('#kcdId').val(kcds.at(0).kcdId);
            kcds.forEach(function (kcd){
                renderKcdForUpdate(kcd);
            });

        }
        else {
            $('#no-kcd').css('display' , 'flex');
        }

        // 처방수가 & 치료재료
        if(treatments.length == 0 && medicalMaterials.length == 0){
                $('#no-pay').css('display' , 'flex');
        }

        if(treatments.length > 0){
            $('#no-pay').css('display' , 'none');
            $('#treatmentId').val(treatments.at(0).treatmentId);

            treatments.forEach(function (treatment){
                renderTreatment(treatment);
            });
        }

        if(medicalMaterials.length > 0){
            $('#no-pay').css('display' , 'none');
            console.log("medicalBillId 설정해줬음" , diagnosis.medicalBillId);
            $('#medicalBillId').val(diagnosis.medicalBillId);

            medicalMaterials.forEach(function (medicalMaterial){
                renderMedical(medicalMaterial);
            });
        }
    }
    // 새로 작성인 경우
    else if(diagnosis.diagnosisYn === false || diagnosis.diagnosisYn === null){
        $('#diagnosis-modify-btn').hide();
        $('#diagnosis-cancel-btn').hide();
        $('#diagnosis-delete-btn').hide();
        $('#diagnosis-record').val("");
        $('#prescription-record').val("");
    }

    // 공통처리 (수정 || 새로작성)

    $('#diagnosis-create-btn').show();  // 등록버튼 활성화
    // 모든 textarea 활성화.
    $('#symptoms-record').attr('readonly' , false);
    $('#diagnosis-record').attr('readonly' , false);
    $('#prescription-record').attr('readonly' , false);

    $('#feeSearch').show();
    $('#medicineSearchBtn').show();
    $('#kcdsSearchBtn').show();

    if(diagnosis.modifyDate !== null){
        $('#diagnosis-write-sub-title').show();
        $('#diagnosis-write-sub-title > span:first-child').text(diagnosis.modifyDate);
        $('#diagnosis-write-sub-title > span:nth-child(3)').text(diagnosis.modifierName);
    }
    $('#todayDiagnosisDate').text(diagnosis.diagnosisDate)  // 작성 시간을 넣어준다.
    $('#diagnosis-write-title > span').show();              // 모든 span 보여주기(날짜:시간, 환자명)
    $('#diagnosis-write-title > span:nth-child(3)').text(diagnosis.patientName);   // 환자명 넣기

    $('#new-diagnosisId').val(diagnosis.diagnosisId);   // hidden에 진료 id 넣기
    $('#new-patientId').val(diagnosis.patientId);       // hidden에 불러온 환자 id 넣기

    var symptomsRecord = diagnosis.symptoms.replace('\r\n' , '<br>');
    $('#symptoms-record').val(symptomsRecord);     // 증상 값 넣기

    // $('#sortable-container').removeClass("ui-sortable-disabled"); // disabled가 적용되있다면 삭제되도록.
    // $('#sortable-container').removeClass("ui-sortable"); // disabled가 적용되있다면 삭제되도록.

    $('#sortable-container').sortable({item: 'ul.kcd'}); // .kcds div안에 .kcd를 갖는 ul 정렬 기능 활성화.

}

function renderKcdForUpdate(kcd){

    var kcdsBox = $('#sortable-container');

    var kcdHtml = $('<ul data-rank="' + kcd.kcdRank + '" data-kcd-seq="' + kcd.kcdSeq +'"></ul>');
    kcdHtml.addClass('kcd');

    var kcdTypeOption = '';
    kcdTypeOption += '<option value="1"' + (kcd.kcdType === 1? 'selected' : '') + '>주상병</option>';
    kcdTypeOption += '<option value="2"' + (kcd.kcdType === 2? 'selected' : '') + '>기타상병</option>';
    kcdTypeOption += '<option value="3"' + (kcd.kcdType === 3? 'selected' : '') + '>배제된상병</option>';

    kcdHtml.append(
        '<li>' + kcd.kcdCode + '</li>' +
        '<li>' + kcd.kcdName + '</li>' +
        '<li><select class="kcdType">' + kcdTypeOption + '</select></li>' +
        '<li onclick="deleteUploadedKcd(this)">X</li>'
    );

    kcdsBox.append(kcdHtml);

}

function deleteUploadedKcd(clickedBox){

    var kcdBox = $(clickedBox).closest('.kcd');
    // var kcdSeq = kcdBox.data('kcd-seq');
    // var kcdsBox = $('#sortable-container');
    // kcdsBox.append('<input type="hidden" class="deleteKcd" value="' + kcdSeq + '">');
    kcdBox.remove();
}

// 수정할때, 의약품 로컬스토리지에 저장해주기
function uploadLocalStorage(){
    localStorage.removeItem('selectedPills');   // 로컬스토리지에 의약품 정보 삭제

    let selectedPills = [];
    $('#pills .pill').each(function (){
        let pill = {
            itemSeq : $(this).find('input').val(),
            itemName: $(this).find('li').eq(0).text(),
            dosage: $(this).find('li').eq(1).find('input').val(),
            frequency: $(this).find('li').eq(2).find('input').val(),
            days: $(this).find('li').eq(3).find('input').val()
        }

        selectedPills.push(pill);
    });

    localStorage.setItem('selectedPills' , JSON.stringify(selectedPills));
}

// 수정할때, 업로드된 파일 보여주기
function renderFileForUpdate(file){
    var fileHtml = $('<div></div>');
    fileHtml.addClass('file');
    fileHtml.attr('data-file-id' , file.fileId);
    fileHtml.attr('data-file-seq', file.fileSeq);

    if(file.img === true){
        fileHtml.append(`
            <div class="thumbnail">
                <img src="/files/${file.link}" alt="${file.orgFileName}" onclick="viewImage('${file.saveFileName}')" />
            </div>
        `);
    }
    else{
        fileHtml.append(`
            <div class="thumbnail">
                <a onclick="viewImage('${file.saveFileName}')">
                    <i class="fa-regular fa-file"></i>
                </a>
            </div>
        `);
    }

    var saveFileName = encodeURIComponent(file.saveFileName);

    fileHtml.append(`
        <div class="file-info">
           <span class="name">${file.orgFileName}</span>
           <span class="size">${formatFileSize(file.fileSize)}</span>
        </div>
        <div onclick="deleteUploadedFile(this)">
                <span>X</span>
        </div>
    `)

    return fileHtml;
}

function deleteUploadedFile(clickedBox){

    var fileBox = $(clickedBox).closest('.file');

    var fileId = fileBox.data('file-id');
    var fileSeq = fileBox.data('file-seq');

    var filesBox =  $('#files');

    filesBox.append(
        `<input type="hidden" class="deleteFile" value="` + fileSeq + `">`
    );

    fileBox.remove();

    // $.ajax({
    //     url: '/files/delete',
    //     type:'post',
    //     contentType : 'application/json; charset=UTF-8',
    //     data : JSON.stringify({
    //         fileId : fileId,
    //         fileSeq: fileSeq
    //     }),
    //     success: function (response){
    //         alert('파일 삭제 처리됨.');
    //         fileBox.remove();
    //     },
    //     error: function (xhr, status, error){
    //         console.error('파일 삭제 실패' , status, error);
    //         alert('파일 삭제에 실패했습니다.\r다음에 다시 시도해주세요.');
    //     }
    // })


}


function clearDiagnosis(){

    var date = new Date();
    var today =  date.getFullYear() + "-" + ("0"+(date.getMonth()+1)).slice(-2) + "-" + ("0"+date.getDate()).slice(-2);
    $('#todayDiagnosisDate').text(today);   // 진료기록 작성창에 오늘 날짜 넣기

    $('#symptoms-record').attr('readonly' , false);
    $('#symptoms-record').val("");
    $('#diagnosis-record').attr('readonly' , false);
    $('#diagnosis-record').val("");
    $('#prescription-record').attr('readonly' , false);
    $('#prescription-record').val("");

    // $('#diagnosis-write-title > span').not(':first-child').hide();
    $('#diagnosis-write-title > span').show();
    $('#diagnosis-write-title > span:nth-child(3)').text('[선택된 환자 없음]');

    $('#new-diagnosisId').val("");     // hidden에 진료 id 초기화
    $('#new-patientId').val("");       // hidden에 불러온 환자 id 초기화
    $('#diagnosisModifier').val("");   // 수정자 초기화
    $('#fileId').val("");              // hidden fileId 초기화
    $('#diagnosisYn').val(false);      // 초기화
    $('#kcdId').val("");                    // hidden kcd 질병기록 id 초기화
    $('#pillId').val("");                       // hidden pillId 의약품 id 초기화
    $('#treatmentId').val("");  // 처방수가 id 초기화
    $('#medicalBillId').val("");    // 치료재료 id 초기화

    uploadFiles = [];   /// uploadFiles 초기화

    $('#files').empty();    // 파일저장목록 html 삭제
    $('#file-drag-box > i').show();
    $('#file-drag-box > p').show();
    $('#file-add').show();
    $('.file-label').show();
    $('#file-read').hide();
    $('#diagnosis-delete-btn').hide();
    $('#diagnosis-cancel-btn').hide();
    $('#diagnosis-modify-btn').hide();
    $('#medicineSearchBtn').hide();
    $('#kcdsSearchBtn').hide();
    $('#feeSearch').hide();
    $('#no-pills').css('display' , 'flex');
    $('#no-files').hide();
    $('#no-kcd').css('display' , 'flex');
    $('#diagnosis-write-sub-title').hide();
    $('#no-pay').css('display' , 'flex');

    localStorage.removeItem('selectedPills');   // 로컬스토리지에 의약품 정보 삭제
    $('#pills').find('.pill').remove();     // 처방 선택된 의약품 html 삭제
    $('#sortable-container .kcd').remove();      // 질병기록 kcd html 삭제
    $('#treatment-box').find('.treatment-data').remove(); // 처방수가 삭제
    $('#medical-box').find('.medical-data').remove(); // 의료수가 삭제
    // $('#sortable-container').sortable("disable");

    // if ($('#sortable-container').data('ui-sortable')) {
    //     $('#sortable-container').sortable("disable");
    // }

    if ($('#sortable-container').data('ui-sortable')) {
        $('#sortable-container').sortable("destroy");
        $('#sortable-container').removeClass("ui-sortable ui-sortable-disabled");
    }
}

function readDiagnosis(response){

    clearDiagnosis();

    console.log('readDiagnosis 실행');
    offFileEvent(); // 파일 이벤트 비활성화

    var diagnosis = response.diagnosisDTO;
    console.log('diagnosis' , diagnosis);
    console.log('response.diagnosisDTO' , response.diagnosisDTO);
    console.log('response' , response);
    // 아래는 js 내장함수 Array의 isArray 배열인지 확인하여 true, false 반환
    var fileInfo = Array.isArray(response.fileInfoDTOList)? response.fileInfoDTOList : [];
    var medicine = Array.isArray(response.medicineDTOList)? response.medicineDTOList : [];
    var kcds = Array.isArray(response.kcdDTOList)? response.kcdDTOList : [];
    var treatments = Array.isArray(response.treatmentDTOList)? response.treatmentDTOList : [];
    var medicalMaterials = Array.isArray(response.medicalMaterialDTOList)? response.medicalMaterialDTOList : [];


    $('#diagnosis-modify-btn').show();
    $('#diagnosis-delete-btn').show();
    $('#diagnosis-create-btn').hide();
    $('#diagnosis-cancel-btn').hide();
    $('#medicineSearchBtn').hide();
    $('#kcdsSearchBtn').hide();

    var symptomsRecord = diagnosis.symptoms.replace('\r\n' , '<br\>');
    var diagnosisRecord = diagnosis.diagnosis.replace('\r\n' , '<br\>');
    var prescriptionRecord = diagnosis.prescription.replace('\r\n' , '<br\>');
    $('#symptoms-record').val(symptomsRecord);     // 증상 값 넣기
    $('#diagnosis-record').val(diagnosisRecord);   // 진료 기록 넣기
    $('#prescription-record').val(prescriptionRecord);    // 처방 기록 넣기

    // 모든 textarea 입력창 readonly로 수정 불가로 처리.
    $('#symptoms-record').attr('readonly' , true);
    $('#diagnosis-record').attr('readonly' , true);
    $('#prescription-record').attr('readonly' , true);

    // hidden input 값 처리, (수정자 id , 환자 id , 진료기록 id)
    if(diagnosis.diagnosisModifier != null){
        $('#diagnosisModifier').val(diagnosis.diagnosisModifier);
    }
    else {
        $('#diagnosisModifier').val("");
    }

    $('#new-diagnosisId').val(diagnosis.diagnosisId);   // hidden에 진료 id 넣기
    $('#new-patientId').val(diagnosis.patientId);       // hidden에 불러온 환자 id 넣기

    // 수정한 날짜시간 존재시, 수정시간, 수정자 보여주기
    if(diagnosis.modifyDate !== null){
        $('#diagnosis-write-sub-title').show();
        $('#diagnosis-write-sub-title > span:first-child').text(diagnosis.modifyDate);
        $('#diagnosis-write-sub-title > span:nth-child(3)').text(diagnosis.modifierName);
    }

    $('#todayDiagnosisDate').text(diagnosis.diagnosisDate)  // 시간을 넣어준다.
    $('#diagnosis-write-title > span').show();              // 모든 span 보여주기(날짜:시간, 환자명)
    $('#diagnosis-write-title > span:nth-child(3)').text(diagnosis.patientName);   // 환자명 넣기

    ///////////////////////////////////////////////////
    // 질병기록 처리
    if(kcds.length > 0){
        $('#no-kcd').hide();

        var kcdBox = $('#sortable-container');

        // a-b 오름차순 정렬
        kcds.sort(function (a , b){
           return a.kcdRank - b.kcdRank;
        });

        kcds.forEach(function (kcd){
           var kcdHtml = readKcdRender(kcd);
           kcdBox.append(kcdHtml);
        });
    }
    else{
        $('#no-kcd').css('display' , 'flex');
    }


    ///////////////////////////////////////////////////
    // 파일 처리
    $('#file-add').hide();
    $('.file-label').hide();
    $('#file-read').show();
    $('#file-drag-box > i').hide();
    $('#file-drag-box > p').hide();
    // 파일 목록이 있을 경우
    if(fileInfo.length > 0){
        var files =  $('#files');

        fileInfo.forEach(function (file){
           var fileHtml = readFileRender(file);
           files.append(fileHtml);
        });
    }
    else {
        $('#no-files').show();
    }

    //////////////////////////////////////////////////
    // 처방한 약이 존재할 경우
    if(medicine.length > 0){
        var pillsBox = $('#pills');
        pillsBox.find('.pill').remove();
        $('#no-pills').css('display' , 'none');

        medicine.forEach(function (pill){

            pillsBox.append(
                '<ul class="pill">' +
                '<input type="hidden" class="itemSeq" value="' + pill.itemSeq + '">' +
                '<li>' + pill.medicineName +'</li>' +
                '<li><input type="text" value="' + pill.dosage + '" autocomplete="off" oninput="checkDouble(this)" readonly></li>' +
                '<li><input type="text" value="' + pill.frequency + '" autocomplete="off" oninput="checkDouble(this)" readonly></li>' +
                '<li><input type="text" value="' + pill.days + '" autocomplete="off" oninput="checkDouble(this)" readonly></li>' +
                '</ul>');
        });
    }
    // 처방된 약이 없을 경우
    else {
        $('#no-pills').css('display' , 'flex');
    }

    ///////////////////////////////////////////////
    // 처방수가 또는 치료재료 기록 존재할 경우

    if(treatments.length == 0 && medicalMaterials.length == 0){
        $('#no-pay').css('display' , 'flex');
    }

    if(treatments.length > 0){
        $('#no-pay').css('display' , 'none');
        treatments.forEach(function (treatment){
        readTreatmentRender(treatment);
        });
    }

    if(medicalMaterials.length > 0){
        $('#no-pay').css('display' , 'none');
        medicalMaterials.forEach(function (medicalMaterial){
            readMedicalMaterialRender(medicalMaterial);
        });
    }



}

// 읽기용 처방수가 정보 넣기
function readTreatmentRender(treatment){

    var treatmentBox = $('#treatment-box');
    treatmentBox.css('display' , 'block');

    var benefitType = treatment.benefitType == true? '급여' : '비급여';
    var benefitClass = treatment.benefitType == true? 'benefit' : 'non-benefit';
    var surgeryYn = treatment.surgeryYn == true? '수술' : '비수술';
    var surgeryClass = treatment.surgeryYn == true? 'surgery' : 'no-surgery';

    var treatmentHtml =
        '<li class="treatment-data">' +
        '<div>' +
        '<span>' + treatment.feeCode + '</span>' +
        '<span>' + treatment.codeName + '</span>' +
        '<span class="' + benefitClass + '">' + benefitType + '</span>' +
        '<span class="' + surgeryClass + '">' + surgeryYn + '</span>' +
        '</div></li>';

    treatmentBox.append(treatmentHtml);
}
// 읽기용 치료재료 정보 넣기
function readMedicalMaterialRender(medicalMaterial){
    var medicalBox = $('#medical-box');
    medicalBox.css('display' , 'block');

    var medicalHtml =
        '<li class="medical-data">' +
        '<div>' +
        '<span>' + medicalMaterial.mmCode + '</span>' +
        '<span>' + medicalMaterial.mmName + '</span>' +
        '</div></li>';

    medicalBox.append(medicalHtml);
}


// 진료작성칸 보기용 파일 리스트 html 처리
function readFileRender(file){
    var fileHtml = $('<div></div>');
    fileHtml.addClass('file');

    if(file.img === true){
        fileHtml.append(`
            <div class="thumbnail">
                <img src="/files/${file.link}" alt="${file.orgFileName}" onclick="viewImage('${file.saveFileName}')" />
            </div>
        `);
    }
    else{
        fileHtml.append(`
            <div class="thumbnail">
                <a onclick="viewImage('${file.saveFileName}')">
                    <i class="fa-regular fa-file"></i>
                </a>
            </div>
        `);
    }

    var saveFileName = encodeURIComponent(file.saveFileName);

    fileHtml.append(`
        <div class="file-info">
           <span class="name">${file.orgFileName}</span>
           <span class="size">${formatFileSize(file.fileSize)}</span>
        </div>
        <div>
            <a href="/files/download/${saveFileName}" download="${file.orgFileName}">
                <i class="fa-solid fa-download"></i>
            </a>
        </div>
    `)

    return fileHtml;
}

function readKcdRender(kcd){

    var kcdHtml = $('<ul data-rank="' + kcd.kcdRank + '"></ul>');
    kcdHtml.addClass('kcd');

    var kcdTypeOption = '';

    switch (kcd.kcdType){
        case 1:
            kcdTypeOption = '주상병';
            break;
        case 2:
            kcdTypeOption = '기타상병';
            break;
        case 3 :
            kcdTypeOption = '배제된상병';
            break;
        default :
            kcdTypeOption = '미선택상병';
    }
    // kcdTypeOption += '<option value="1"' + (kcd.kcdType === 1? 'selected' : '') + '>주상병</option>';
    // kcdTypeOption += '<option value="2"' + (kcd.kcdType === 2? 'selected' : '') + '>기타상병</option>';
    // kcdTypeOption += '<option value="3"' + (kcd.kcdType === 3? 'selected' : '') + '>배제된상병</option>';

    kcdHtml.append(
        '<li>' + kcd.kcdCode + '</li>' +
        '<li>' + kcd.kcdName + '</li>' +
        '<li class="read-kcd">' + kcdTypeOption + '</li>'
        // '<li><select class="kcdType" disabled>' + kcdTypeOption + '</select></li>'
    );

    return kcdHtml;
}

function getKcdPopup(){

    var patientId = $('#new-patientId').val();

    if(patientId === null || patientId === ''){
        alert('환자 정보를 먼저 설정 해주세요.');
        return;
    }

    var popup = window.open(
        `/kcd-search`,
        '질병분류코드 검색',
        'width=800 , height=531 , screenX=500,screenY=100, resizeable=no'
    );
}

function receiveKcdInfo(kcd){

    var kcdObj = JSON.parse(kcd);
    var kcdCode= kcdObj.kcdCode;
    var kcdName = kcdObj.kcdName;

    console.log(kcdCode);
    console.log(kcdName);

    $('#no-kcd').hide();

    var kcdsBox = $('#sortable-container');

    var kcdHtml = $('<ul data-rank=""></ul>');
    kcdHtml.addClass('kcd');


    kcdHtml.append(
        '<li>' + kcdCode + '</li>' +
        '<li>' + kcdName + '</li>' +
        '<li><select class="kcdType">' +
            '<option value="1">주상병' + '</option>' +
            '<option value="2">기타상병' + '</option>' +
            '<option value="3">배제된상병' + '</option>' +
        '</select></li>' +
        '<li onclick="deleteKcd(this)">X</li>'
    );

    kcdsBox.append(kcdHtml);
}

function deleteKcd(li){
    console.log('kcd 삭제처리 해줘야함.')
    var kcd = li.closest('ul');
    kcd.remove();
}


function getMedicinePopup(){

    var patientId = $('#new-patientId').val();

   if(patientId === null || patientId === ''){
       alert('환자 정보를 먼저 설정 해주세요.');
       return;
   }

    var popup = window.open(
       '/medicine-prescription' ,
       '의약품 검색',
       'width=1660 ,  height=865 , top=100 , right=100 , resizeable=no');
}

function viewImage(saveFileName){
    var imagePopup = window.open('/files/' + saveFileName , '_blank');
    imagePopup.focus();
}

function downloadFile(saveFileName){
    window.location.href = '/files/' + saveFileName;
}


// 확인 버튼을 눌러 팝업창이 닫히면 로컬스토리지의 데이터를 바탕으로 pills div에 내용을 넣어준다.
function receiveMedicineInfo(){

    var selectedPills = JSON.parse(localStorage.getItem('selectedPills')) || [];

    if(selectedPills !== []){

        console.log('selectedPills' , selectedPills);

        var pillsBox = $('#pills');
        pillsBox.find('.pill').remove();

        if(selectedPills.length <= 0){
            $('#no-pills').css('display' , 'flex');
            return;
        }

        $('#no-pills').css('display' , 'none');

        selectedPills.forEach(function (pill){

            pillsBox.append(
                '<ul class="pill">' +
                '<input type="hidden" class="itemSeq" value="' + pill.itemSeq + '">' +
                '<li>' + pill.itemName +'</li>' +
                '<li><input type="text" value="' + pill.dosage + '" autocomplete="off" oninput="checkDouble(this)"></li>' +
                '<li><input type="text" value="' + pill.frequency + '" autocomplete="off" oninput="checkDouble(this)"></li>' +
                '<li><input type="text" value="' + pill.days + '" autocomplete="off" oninput="checkDouble(this)"></li>' +
                '</ul>');
        });
    }
    else {
        $('#no-pills').css('display' , 'flex');
    }
}


function renderPills(pill){

    var pillsBox = $('#pills');

    pillsBox.append(
        '<ul class="pill">' +
        '<input type="hidden" class="itemSeq" value="' + pill.itemSeq + '">' +
        '<li>' + pill.medicineName +'</li>' +
        '<li><input type="text" value="' + pill.dosage + '" autocomplete="off" oninput="checkDouble(this)"></li>' +
        '<li><input type="text" value="' + pill.frequency + '" autocomplete="off" oninput="checkDouble(this)"></li>' +
        '<li><input type="text" value="' + pill.days + '" autocomplete="off" oninput="checkDouble(this)"></li>' +
        '</ul>');

}

// 수가&치료재료 검색 팝업창 열기.
function getTreatmentPopup(){

    var patientId = $('#new-patientId').val();

    if(patientId === null || patientId === ''){
            alert('환자 정보를 먼저 설정 해주세요.');
            return;
    }

    var popup = window.open(
        `/treatment-search`,
        '수가검색',
        'width=800 , height=535 , screenX=500,screenY=100, resizeable=no'
    );

//    postTreatmentDataToPopup(popup); // 수가&치료재료 팝업창으로 기존 데이터 보내기

}

//function postTreatmentDataToPopup(popup){
//
//    var treatmentList = [];
//
//    $('#treatment-box .treatment-data').each(function(){
//
//        var item = $(this);
//
//        treatmentList.push({
//            feeCode : item.find('span:nth-child(1)').text(),
//            codeName : item.find('span:nth-child(2)').text(),
//            benefitType : item.find('.benefit').text(),
//            surgery: item.find('.no-surgery').text(),
//            costScore: item.find('.costScore').val(),
//            benefitType: item.find('.benefitType').val(),
//            unitPrice: item.find('.unitPrice').val(),
//            feeDivNum: item.find('.feeDivNum').val(),
//            surgeryYn: item.find('.surgeryYn').val(),
//            deductibleA: item.find('.deductibleA').val(),
//            deductibleB: item.find('.deductibleB').val(),
//            startDate: item.find('.startDate').val()
//        });
//    });
//
//
//    var medicalList = [];
//
//    $('#medical-box .medical-data').each(function(){
//
//         medicalList.push({
//            mmId : $(this).find('.mmId').val(),
//            mmCode :  $(this).find('span').eq(0).text(),
//            mmName:  $(this).find('span').eq(1).text()
//         });
//    });
//
//
//    var data = {
//        treatments : treatmentList,
//        medicals : medicalList
//    }
//
//    popup.postMessage(data , "*"); // 열려 있는 모든 팝업창에 데이터 보내기로 됨.
//
//}

// 수가&치료재료 검색 팝업창으로 부터 정보를 받아 #pay 칸에 html로 만들어 넣어준다.
function receiveTreatmentInfo(allData) {
    console.log('allData : ' , allData);

    var treatmentList = Array.isArray(allData.treatment)? allData.treatment : [];
    var medicalList = Array.isArray(allData.medical)? allData.medical : [];
    // 둘 중 하나라도 받아온 데이터가 있다면 기존 데이터 모두 삭제.
    if(treatmentList.length > 0 || medicalList.length > 0){
        $('#no-pay').css('display' , 'none');
        $('#pay ul').empty();  // 하위 html 모두 삭제
    }

    if(treatmentList.length > 0){
        treatmentList.forEach(function(treatment){
            renderTreatment(treatment);
        });
    }

    if(medicalList.length > 0){
        medicalList.forEach(function(medical){
            renderMedical(medical);
        });
    }

}
// #pay 칸에 처방수가 정보 html로 넣어준다.
function renderTreatment(treatment){

    var treatmentBox = $('#treatment-box');
    treatmentBox.css('display' , 'block');

    var benefitType = treatment.benefitType == true? '급여' : '비급여';
    var benefitClass = treatment.benefitType == true? 'benefit' : 'non-benefit';
    var surgeryYn = treatment.surgeryYn == true? '수술' : '비수술';
    var surgeryClass = treatment.surgeryYn == true? 'surgery' : 'no-surgery';

    var treatmentHtml =
        '<li class="treatment-data">' +
        '<div>' +
        '<span>' + treatment.feeCode + '</span>' +
        '<span>' + treatment.codeName + '</span>' +
        '<span class="' + benefitClass + '">' + benefitType + '</span>' +
        '<span class="' + surgeryClass + '">' + surgeryYn + '</span>' +
        '<input type="hidden" class="costScore" value="' + treatment.costScore + '">' +
        '<input type="hidden" class="benefitType" value="' + treatment.benefitType +'">' +
        '<input type="hidden" class="unitPrice" value="' + treatment.unitPrice + '">' +
        '<input type="hidden" class="feeDivNum" value="' + treatment.feeDivNum + '">' +
        '<input type="hidden" class="surgeryYn" value="' + treatment.surgeryYn + '">' +
        '<input type="hidden" class="deductibleA" value="' + treatment.deductibleA + '">' +
        '<input type="hidden" class="deductibleB" value="' + treatment.deductibleB + '">' +
        '<input type="hidden" class="startDate" value="' + treatment.startDate + '">' +
        '</div>' +
        '<div class="delete-data">X</div>'
        '</li>';

    treatmentBox.append(treatmentHtml);
}
// #pay 칸에 치료재료 정보 html로 넣어준다.
function renderMedical(medical){

    var medicalBox = $('#medical-box');
    medicalBox.css('display' , 'block');

    var medicalHtml =
        '<li class="medical-data">' +
        '<div>' +
        '<span>' + medical.mmCode + '</span>' +
        '<span>' + medical.mmName + '</span>' +
        '<input type="hidden" class="mmId" value="' + medical.mmId + '">' +
        '</div>' +
        '<div class="delete-data">X</div>' +
        '</li>';

    medicalBox.append(medicalHtml);
}

$('#pay ul').on('click' , '.delete-data' , function(){
    $(this).closest('li').remove();
});

// 처방수가 정보 모아 반환하는 함수
function collectTreatment(){

    var treatmentList = [];

    $('#treatment-box .treatment-data').each(function(){
        var item = $(this);
        var treatment = {
            feeCode : item.find('span:nth-child(1)').text(),
            codeName : item.find('span:nth-child(2)').text(),
            benefit: item.find('.benefit').text(),
            surgery: item.find('.no-surgery').text(),
            costScore: item.find('.costScore').val(),
            benefitType: item.find('.benefitType').val(),
            unitPrice: item.find('.unitPrice').val(),
            feeDivNum: item.find('.feeDivNum').val(),
            surgeryYn: item.find('.surgeryYn').val(),
            deductibleA: item.find('.deductibleA').val(),
            deductibleB: item.find('.deductibleB').val(),
            startDate: item.find('.startDate').val()
        }
        treatmentList.push(treatment);
    });

    return treatmentList;
}
// 치료재료 정보 모아 반환하는 함수
function collectMedical() {

    var medicalList = [];

    $('#medical-box .medical-data').each(function(){
        var item = $(this);
        var medical = {
            mmId : item.find('.mmId').val(),
            mmCode : item.find('span:nth-child(1)').text(),
            mmName : item.find('span:nth-child(2)').text()
        }
        medicalList.push(medical);
    });

    return medicalList;
}


