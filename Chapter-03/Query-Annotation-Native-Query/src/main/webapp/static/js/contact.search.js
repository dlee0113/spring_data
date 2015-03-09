$(function () {
    function search(searchCondition) {

        Contact.storeSearchConditionsToCache(searchCondition);
        Contact.search("/contact/search", searchCondition, Contact.storeSearchConditionsToCache);
    };

    function parseSearchCondition() {
        var searchCondition = Contact.getSearchConditionsFromCache();
        if (!searchCondition) {
            searchCondition = {};
            searchCondition.searchTerm = $("#search-term").text();
        }
        return searchCondition;
    }


    var searchCondition = parseSearchCondition();
    search(searchCondition);
});
