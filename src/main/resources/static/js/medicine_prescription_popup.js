// https://velog.io/@ggong/MutationObserver
// https://adjh54.tistory.com/56
// chatGPT

$(document).ready(function (){
    // html이 모두 로드 되면,
    // 로컬스토리지로부터 selectedPills(key)에 해당하는 value들을 가져오고
    // 없다면 빈 배열로 초기화하게 된다.
    var selectedPills = JSON.parse(localStorage.getItem('selectedPills')) || [];

    if(selectedPills === []){
        $('#no-pills').css('display' , 'flex');
    }
    else {
        $('#no-pills').css('display' , 'none');
    }

    // selectedPills 배열에 대하여 향상된 for문으로 각각의 pill들을 html로 만들어 추가해주도록 한다.
    selectedPills.forEach(function (pill){
       addPillToPillsBox(pill);
    });

});

// #pills에 로컬스토리지 정보를 이용하여 pill html을 만들어 추가해주는 함수
function addPillToPillsBox(pill){
    var pillsBox = $('#pills');

    pillsBox.append(
        '<ul class="pill">' +
            '<input type="hidden" value="' + pill.itemSeq + '">' +
            '<li>' + pill.itemName +'</li>' +
            '<li><input type="text" value="' + pill.dosage + '" autocomplete="off" oninput="checkDouble(this)"></li>' +
            '<li><input type="text" value="' + pill.frequency + '" autocomplete="off" oninput="checkDouble(this)"></li>' +
            '<li><input type="text" value="' + pill.days + '" autocomplete="off" oninput="checkDouble(this)"></li>' +
            '<li>X</li>' +
        '</ul>');
}

// 자식 노드 변경 감지하는 api, 변경 발생시 콜백함수 실행
const observer = new MutationObserver(function (mutations){
   console.log('mutations' , mutations);
   console.log('observer' , observer);

   // 변경사항 표현하는 mutations 객체 형태 배열에서
   mutations.forEach(function (mutation){
       // 변경사항의 타입이 자식노드이고 변경사항으로 추가되거나 제거된 노드가 0개 이상이라면 로컬스토리지에 업데이트 함수 실행.
      if(mutation.type === 'childList' && mutation.addedNodes.length > 0){
          updateLocalStorage();
      }
   });
});

// 감지대상 설정
const targetNode = document.getElementById('pills');
// 감지대상의 자식노드 변경 감시 설정
observer.observe(targetNode , {
    childList: true
});

// 로컬스토리지에 업데이트 해주는 함수
function updateLocalStorage(){
    // 빈 배열로 초기화
    let selectedPills = [];
    // #pills 안에 있는 .pill(ul)에 대하여
    // 각각의 li 안에 값들을 가져와 json 형태로 만들어주고
    // 초기화 했던 배열, selectedPills에 넣어준다.
    $('#pills .pill').each(function (){
        let pill = {
            itemSeq : $(this).find('input').val(),
            itemName: $(this).find('li').eq(0).text(),
            dosage: $(this).find('li').eq(1).find('input').val(),
            frequency: $(this).find('li').eq(2).find('input').val(),
            days: $(this).find('li').eq(3).find('input').val()
        };

        selectedPills.push(pill);
    });

    // 마지막으로 selectedPills라는 key로 로컬스토리지에 json으로 넣어준다.
    localStorage.setItem('selectedPills' , JSON.stringify(selectedPills));
}

// #pills(div) 박스 안에 있는 li 마지막 요소들에 대하여 클릭이벤트가 일어난다면,
// 해당 클릭이벤트 객체로부터 가장 가까운 .pill(ul)을 삭제하도록 해라.
// 이후 로컬스토리지에 업데이트를 진행.
$('#pills').on('click', 'li:last-child', function() {
    $(this).closest('.pill').remove();
    // 어차피 옵저버가 감시대상인 #pills의 자식노드가 삭제되었음을 감지하여 updateLocalStorage()를
    // 실행할 것인데, 함수를 실행하는 이유는 즉각적으로 업데이트를 함으로서
    // 일관성을 높이고 즉시 반영되도록 하기 위함이다. 물론 이후에 다시 옵저버가 콜백함수로서 실행하게 된다.
    updateLocalStorage();
});
// input에 값이 바뀔 때마다 없데이트
$('#pills').on('input' , 'input' , function (){
    updateLocalStorage();
});

// pills에 pill html 추가해주는 함수
function clickPill(btn){

    $('#no-pills').css('display' , 'none');

    var tr = btn.closest('tr');
    var tds = tr.getElementsByTagName('td');

    var itemSeq = tds[0].innerText;
    var itemName = tds[1].innerText;

    var pillsBox = $('#pills');

    pillsBox.append(
        '<ul class="pill">' +
        '<input type="hidden" value="' + itemSeq + '">' +
        '<li>' + itemName +'</li>' +
        '<li><input type="text" value="0" autocomplete="off" oninput="checkDouble(this)"></li>' +
        '<li><input type="text" value="0" autocomplete="off" oninput="checkDouble(this)"></li>' +
        '<li><input type="text" value="0" autocomplete="off" oninput="checkDouble(this)"></li>' +
        '<li>X</li>' +
        '</ul>');

    updateLocalStorage();
}

function checkDouble(input){
    input.value = input.value.replace(/[^0-9.]/g, '').replace(/(\..*)\./g, '$1');
}

// pills 안에 모든 내용 삭제
function deletePills(){

    $('#pills').empty();
    $('#no-pills').css('display' , 'flex');

    updateLocalStorage();
}

// 팝업창 닫기
function closePopup(){
    // 로컬스토리지를 사용했기 때문에 전달할 인자는 없고 함수사용만 하도록
    opener.parent.receiveMedicineInfo();
    window.self.close();
}



$('#search-btn').click(function() {
    search();
});

$('#search-bar').keypress(function(e){
    if(e.which === 13){
        search();
    }
});

function search(){

    var keyword = $('#search-bar').val();
    var type = $('#type').val();

    if(keyword === null || keyword == ''){
        alert('검색어를 입력해주세요.');
        return ;
    }

    location.href = '/medicine-prescription?type=' + type + '&keyword=' + encodeURIComponent(keyword);
}