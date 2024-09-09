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
    var medTp = $('#medTp').val();
    var sickType = $('#sickType').val();
    var diseaseType = $('#diseaseType').val();

    if(keyword === null || keyword == ''){
        alert('검색어를 입력해주세요.');
        return ;
    }

    location.href = '/kcd-search?' +
                    'medTp=' +  medTp +
                    '&sickType=' + sickType +
                    '&diseaseType=' + diseaseType +
                    '&keyword=' + encodeURIComponent(keyword);

}

function clickKcd(button){

    var row = button.closest('tr');
    var kcdCode = row.querySelector( 'td:nth-child(1)').innerText;
    var kcdName = row.querySelector('td:nth-child(2)').innerText;

    console.log(kcdCode);
    console.log(kcdName);

    var kcd = {
        kcdCode : kcdCode,
        kcdName : kcdName
    }

    opener.parent.receiveKcdInfo(JSON.stringify(kcd));
    window.self.close();
}