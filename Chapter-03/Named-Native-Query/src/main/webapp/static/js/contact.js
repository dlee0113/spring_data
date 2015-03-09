var messageCacheOptions = {
    expires: 30000
};

var pageSpecificationCacheOptions = {
    expires: 300000
};

var searchConditionCacheOptions = {
    expires: 300000
};

var Contact = {
    addErrorMessage:function (message) {
        var alertTemplate = Handlebars.compile($("#template-alert-message-error").html());
        $("#message-holder").html(alertTemplate({message:message}));
        $("#alert-message-error").alert().delay(5000).fadeOut("fast", function() { $(this).remove(); });
    },

    addMessage:function (message) {
        var alertTemplate = Handlebars.compile($("#template-alert-message").html());
        $("#message-holder").html(alertTemplate({message:message}));
        $("#alert-message").alert().delay(5000).fadeOut("fast", function() { $(this).remove(); })
    },
    clearContactPageSpecificationFromCache: function() {
        amplify.store(Contact.getContactPageSpecificationCacheKey(), null);
    },
    clearSearchConditionsFromCache: function() {
        amplify.store(Contact.getSearchConditionCacheKey(), null);
    },
    getContactPageSpecificationCacheKey: function() {
        return "contacts.page.specification";
    },
    getContactPageSpecificationFromCache: function() {
        return amplify.store(Contact.getContactPageSpecificationCacheKey());
    },
    getErrorMessageCacheKey: function() {
        return "contacts.errorMessage";
    },
    getErrorMessageFromCache: function() {
        var errorMessage = amplify.store(Contact.getErrorMessageCacheKey());
        amplify.store(Contact.getErrorMessageCacheKey(), null);
        return errorMessage;
    },
    getMessageCacheKey: function() {
        return "contacts.message";
    },
    getMessageFromCache: function() {
        var message = amplify.store(Contact.getMessageCacheKey());
        amplify.store(Contact.getMessageCacheKey(), null);
        return message;
    },
    storeContactPageSpecificationToCache: function(pageSpecification) {
        amplify.store(Contact.getContactPageSpecificationCacheKey(), pageSpecification, pageSpecificationCacheOptions);
    },
    storeErrorMessageToCache: function(message) {
        amplify.store(Contact.getErrorMessageCacheKey(), message, messageCacheOptions);
    },
    storeMessageToCache: function(message) {
        amplify.store(Contact.getMessageCacheKey(), message, messageCacheOptions);
    },
    getSearchConditionCacheKey: function() {
        return "contact.search.conditions";
    },
    getSearchConditionsFromCache: function() {
        return amplify.store(Contact.getSearchConditionCacheKey());
    },
    storeSearchConditionsToCache: function(searchConditions) {
        amplify.store(Contact.getSearchConditionCacheKey(), searchConditions, searchConditionCacheOptions)
    },
    search: function(url, searchCondition, cacheCallback) {
        var searchResultHolder = $("#contact-list");
        searchResultHolder.children().remove();
        $.ajax({
            contentType:"application/json",
            type:"POST",
            url: url,
            data:JSON.stringify(searchCondition),
            success:function (results) {
                var resultTemplate = Handlebars.compile($("#template-contact-list").html());
                searchResultHolder.append(resultTemplate({results:results}));

                if (results.length === 0) {
                    if ($("#result-count").text() == "0") {
                        //There are no contats at all.
                        $("#contact-list-filters").addClass("hidden");
                        $(".pagination-holder").addClass("hidden");
                    }
                    else {
                        //There are contacts but not on this page. Moving to previous page.
                        searchCondition.pageIndex = searchCondition.pageIndex - 1;
                        cacheCallback(searchCondition);
                        window.location.href = '';
                    }
                }
                else {
                    $("#contact-list-filters").removeClass("hidden");
                    $(".pagination-holder").removeClass("hidden");
                }
            }
        });
    }
};

$(document).ready(function () {

    var errorMessage = $(".errroblock");
    if (errorMessage.length > 0) {
        Contact.addErrorMessage(errorMessage.text());
    }
    else {
        errorMessage = Contact.getErrorMessageFromCache();
        if (errorMessage) {
            Contact.addErrorMessage(errorMessage);
        }
    }

    var feedbackMessage = $(".messageblock");
    if (feedbackMessage.length > 0) {
        Contact.addMessage(feedbackMessage.text());
    }
    else {
        feedbackMessage = Contact.getMessageFromCache();
        if (feedbackMessage) {
            Contact.addMessage(feedbackMessage);
        }
    }

    $("#contact-search-form").on("keypress", "#contact-search-query", function(e) {
        if (e.keyCode === 13) {
            e.preventDefault();
            Contact.clearSearchConditionsFromCache();
            window.location.href = "/contact/search/" + $("#contact-search-query").val();
        }
    })

    $("#navi-homepage-link").click(function(e) {
        Contact.clearContactPageSpecificationFromCache();
    })
});

$(document).bind('ajaxError', function(error, response) {
    if (response.status == "404") {
        window.location.href = "/error/404";
    }
    else {
        window.location.href = "/error/error";
    }
});
