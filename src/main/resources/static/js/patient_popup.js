$('#search-btn').click(function() {
    search();
});

$('#search-bar').keypress(function(e){
    if(e.which === 13){
        search();
    }
});

function search(){
    var patinetName = $('#search-bar').val();

    if(patinetName === null || patinetName == ''){
        alert('검색하실 환자이름을 입력해주세요.');
        return ;
    }

    location.href = '/patient-search/' + encodeURIComponent(patinetName);
}

