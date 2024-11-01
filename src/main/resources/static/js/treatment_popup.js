$(document).ready(function() {
    // search-bar 검색창에 focus나 input 이벤트 발생시, 결과박스가 300ms로 슬라이드해서 내려오도록.
    $('#search-bar').on('focus input', function() {
        // 결과박스 멈춤 설정, 애니메이션 중복 및 대기 삭제
        $('#search-output').stop(true, true).slideDown(300); // 결과박스 애니메이션으로 표시
    });
    // 만약 검색창이 focus를 잃게 되면 blur 이벤트 발생하는데, 그때는 200ms로 결과박스 감추기.
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

    $('#treatmentBox , #medicalBox').on('click' , '.treatment-data , .medical-data' , function(){
        console.log('클릭됨');
        var selectedData = $(this).html();
        $('#selected-box ul').append(
            '<li><div>' + selectedData + '</div>' +
            '<div class="delete-btn">X</div></li>');
    });


    // 클릭 이벤트 li에 발생시, 모든 clicked 클래스 삭제, 클릭된 객체에 클릭 클래스 추가.
    // 클릭된 li에 따라 데이터 박스 활성화 여부 변경.
    $("#search-nav li").click(function(){
        // 기존의 clicked 클래스 제거
        $("#search-nav li").removeClass("clicked");
        // 클릭된 li에 클래스 추가.
        $(this).addClass("clicked");
        // 클릭된 li의 값 가져와서 type에 초기화.
        var type = $(this).attr('value');
        // type의 값에 따라 데이터 박스 활성화 변경.
        if(type == 1){
            $('#treatmentBox').css('display' , 'block');
            $('#medicalBox').css('display' , 'block');
        }
        else if(type == 2){
            $('#treatmentBox').css('display' , 'block');
            $('#medicalBox').css('display' , 'none');
        }
        else if(type == 3){
            $('#treatmentBox').css('display' , 'none');
            $('#medicalBox').css('display' , 'block');
        }
    });
});

// selected-box에서 클릭이벤트가 발생한 요소의 class가 delete-btn이라면 삭제.
$('#selected-box').on('click' , '.delete-btn' , function(){
    $(this).closest('li').remove();
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

    // 시간 지연처리 함수로 ajax를 실행하도록 한다. (지연 0.5초 , 500ms)
    delayTimeout = setTimeout(function(){

    // 서버로 type과 keyword를 보낸다.
    $.ajax({
            url: '/treatment-search?type=' + type + '&keyword=' + keyword,
            type: 'post',
            contentType: 'application/json; charset=UTF8',
            success: function(response){
                console.log('성공');
                console.log('response' , response);
                insertData(response);
            },
            error : function(xhr , status, error){
                console.log('ajax 실패' , status , error);
            }

        })

    } , 500);
}

// 받은 검색결과 데이터를 넣어준다.
function insertData(response){
    console.log('insertDate() 실행');

    var type = $('#search-nav li.clicked').attr('value');   // li에 value로 지정해둔 값을 가져온다.
    var treatmentList = Array.isArray(response.treatmentDTOList)? response.treatmentDTOList : [];
    var medicalMaterialList = Array.isArray(response.medicalMaterialDTOList)? response.medicalMaterialDTOList : [];

    clearAll(); // 모든 기존 데이터 삭제.

    // 전체, 수가, 치료재료 중 선택된 타입으로 검색결과를 채워준다.
    if(type == 1){
        insertTreatment(treatmentList);
        insertMedicalMaterial(medicalMaterialList);
    }
    else if(type == 2){
        insertTreatment(treatmentList);
    }
    else if(type == 3){
        insertMedicalMaterial(medicalMaterialList);
    }

}

// 진료수가 데이터 또는 검색결과 없음 표시
function insertTreatment(treatmentList){
    console.log('insertTreatment() 실행');
    // 배열의 길이가 0 이상(데이터 존재)
    if(treatmentList.length > 0){

        let dataHtmlBox = "";    // 만들어진 html을 담을 변수

        treatmentList.forEach(function(treatment){

            var benefitType = treatment.benefitType == true? '급여' : '비급여';
            var benefitClass = treatment.benefitType == true? 'benefit' : 'non-benefit';

            var surgeryYn = treatment.surgeryYn == true? '수술' : '비수술';
            var surgeryClass = treatment.surgeryYn == true? 'surgery' : 'no-surgery';

            dataHtmlBox +=
                '<li class="treatment-data">' +
                '<span>' + treatment.feeCode + '</span>' +
                '<span>' + treatment.codeName + '</span>' +
                '<span class="' + benefitClass + '">' + benefitType + '</span>' +
                '<span class="' + surgeryClass + '">' + surgeryYn + '</span>' +
                // 외부 공공데이터 api에 요청보내서 가져오는거라서, 여기서 한번에 데이터를 몽땅 들고 서버로 가져가는게 좋을 것 같음.
                '<input type="hidden" class="costScore" value="' + treatment.costScore + '">' +
                '<input type="hidden" class="benefitType" value="' + treatment.benefitType +'">' +
                '<input type="hidden" class="unitPrice" value="' + treatment.unitPrice + '">' +
                '<input type="hidden" class="feeDivNum" value="' + treatment.feeDivNum + '">' +
                '<input type="hidden" class="surgeryYn" value="' + treatment.surgeryYn + '">' +
                '<input type="hidden" class="deductibleA" value="' + treatment.deductibleA + '">' +
                '<input type="hidden" class="deductibleB" value="' + treatment.deductibleB + '">' +
                '<input type="hidden" class="startDate" value="' + treatment.startDate + '">' +
                '</li>';
        })

        $('#treatmentBox li').last().after(dataHtmlBox);    // 검색결과들을 넣어준다.

    } // if문 end
    else { // 배열의 길이가 0 또는 이하 (데이터 없음)
        $('#treatment-no-data').css('display' , 'flex');
    }

}
// 치료재료 데이터 또는 검색결과 없음 표시
function insertMedicalMaterial(medicalMaterialList){
    console.log('insertMedicalMaterial() 실행.');
    // 배열의 길이가 0 이상(데이터 존재)
    if(medicalMaterialList.length > 0){

        let dataHtmlBox = "";    // 만들어진 html 담을 변수

        medicalMaterialList.forEach(function(medical){

            dataHtmlBox +=
                '<li class="medical-data">' +
                '<span>' + medical.mmCode + '</span>' +
                '<span>' + medical.mmName + '</span>';

            if(medical.distributor != null){
                dataHtmlBox += '<span class="entpNm">' + medical.distributor + '</span>' +
                    '<input type="hidden" class="mmId" value="' + medical.mmId + '"></li>';
            }
            else {
                dataHtmlBox += '</li>';
            }

        })

        $('#medicalBox li').last().after(dataHtmlBox);  // 검색결과들을 넣어준다.

    } // if문 end
    else {
        $('#material-no-data').css('display' , 'flex');
    }

}
// 진료수가 데이터 칸 비우기
function clearTreatmentData() {
    $('#treatmentBox .treatment-data').remove();
    $('#treatment-no-data').hide();
}
// 치료재료 데이터 칸 비우기
function clearMedicalData() {
    $('#medicalBox .medical-data').remove();
    $('#material-no-data').hide();
}
// 모든 검색결과 데이터 창 비우기
function clearAll() {
    clearTreatmentData();
    clearMedicalData();
}

