$(function () {

    function findCount(callback) {
        $.ajax({
            contentType:"application/json",
            type:"POST",
            url:"/contact/search/count",
            data:JSON.stringify(searchCondition),
            success:function (result) {
                $("#result-count").text(result);
                if (result === 0) {
                    $("#result-count-holder").addClass("hidden");
                }
                else {
                    $("#result-count-holder").removeClass("hidden");
                }
                callback(parseInt(result, 10));
            }
        });
    }

    function initContactCountPerPageSelect() {
        $("#contacts-per-page-select").val(searchCondition.pageSize);

        $("#contacts-per-page").on("change", "#contacts-per-page-select", function(e) {
            searchCondition.pageIndex = 0;
            searchCondition.pageSize = e.target.value;

            findCount(initPaginator);
        })
    }

    var initPaginator = function (contactCount) {
        $(".pagination-holder").pagination(contactCount, {
            current_page:searchCondition.pageIndex,
            items_per_page:searchCondition.pageSize,
            load_first_page:true,
            next_show_always:false,
            next_text:$("#pagination-next-label").text(),
            callback:handlePaginationClick,
            prev_show_always:false,
            prev_text:$("#pagination-previous-label").text()
        });
    };

    var handlePaginationClick = function (new_page_index, pagination_container) {
        searchCondition.pageIndex = new_page_index;

        Contact.storeSearchConditionsToCache(searchCondition);

        Contact.search("/contact/search", searchCondition, Contact.storeSearchConditionsToCache);
    };

    function parseSearchCondition() {
        var searchCondition = Contact.getSearchConditionsFromCache();
        if (!searchCondition) {
            searchCondition = {};
            searchCondition.pageIndex = 0;
            searchCondition.pageSize = 10;
            searchCondition.searchTerm = $("#search-term").text();
        }
        return searchCondition;
    }


    var searchCondition = parseSearchCondition();

    findCount(initPaginator);

    initContactCountPerPageSelect();
});
