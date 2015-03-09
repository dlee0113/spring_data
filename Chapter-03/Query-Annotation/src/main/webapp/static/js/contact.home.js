$(function() {
    $("#navi-homepage-link").addClass("active");

    function findCount(callback) {
        $.ajax({
            contentType:"application/json",
            type:"POST",
            url:"/contact/count",
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
        $("#contacts-per-page-select").val(pageSpecification.pageSize);

        $("#contacts-per-page").on("change", "#contacts-per-page-select", function(e) {
            pageSpecification.pageIndex = 0;
            pageSpecification.pageSize = e.target.value;

            findCount(initPaginator);
        })
    }

    var initPaginator = function (contactCount) {
        $(".pagination-holder").pagination(contactCount, {
            current_page:pageSpecification.pageIndex,
            items_per_page:pageSpecification.pageSize,
            load_first_page:true,
            next_show_always:false,
            next_text:$("#pagination-next-label").text(),
            callback:handlePaginationClick,
            prev_show_always:false,
            prev_text:$("#pagination-previous-label").text()
        });
    };

    var handlePaginationClick = function (new_page_index, pagination_container) {
        pageSpecification.pageIndex = new_page_index;

        Contact.storeContactPageSpecificationToCache(pageSpecification);

        Contact.search("/contact/list", pageSpecification, Contact.storeContactPageSpecificationToCache);
    };

    function parsePageSpecification() {
        var pageSpecification = Contact.getContactPageSpecificationFromCache();
        if (!pageSpecification) {
            pageSpecification = {};
            pageSpecification.pageIndex = 0;
            pageSpecification.pageSize = 10;
        }
        return pageSpecification;
    }

    var pageSpecification = parsePageSpecification();

    findCount(initPaginator);

    initContactCountPerPageSelect();
});
