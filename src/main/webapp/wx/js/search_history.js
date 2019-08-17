var searchHistoryKey='search-his';
function addSearchHistory(keyword) {
    var history=localStorage.getItem(searchHistoryKey);
    if(history){
        history=JSON.parse(history);
        for(var i=0 ;i< history.length; i++){
            if(history[i]==keyword){
                history.splice(i, 1);
                break;
            }
        }
        history.push(keyword);
    }else{
        history=[];
        history.push(keyword);
    }
    localStorage.setItem(searchHistoryKey, JSON.stringify(history));
}

function clearSearchHistory() {
    localStorage.setItem(searchHistoryKey, '[]');
}

function getSearchHistory() {
    var history=localStorage.getItem(searchHistoryKey);
    if(history){
        history=JSON.parse(history);
        history.reverse();
        return history;
    }else{
        return [];
    }
}

