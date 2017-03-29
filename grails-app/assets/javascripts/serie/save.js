$(document).ready(function() {

    // https://github.com/biggora/bootstrap-ajax-typeahead
    $('#actorsSearch').typeahead({
        ajax: $('#actorsSearchURL').val(),
        triggerLength: 3,
        onSelect: function(item){
            var actorsDivContent = $('#actorsDiv').html();
            console.log(item);
            actorsDivContent = actorsDivContent + '<li class="list-group-item"><span class="glyphicon glyphicon-user"></span> '
                + item.text +
                ' <a href="#" class="btn btn-danger pull-right deleteActor" onclick="deleteActorDiv(\'' + item.value + '\');"><span class="glyphicon glyphicon-trash"/></a><input type="hidden" id="actors" name="actors" value="' + item.value + '"/> </li>' ;
            $('#actorsDiv').html(actorsDivContent);
            $('#actorsSearch').val('');
        }
    });


    $("#cover").fileinput({
        maxFileCount: 1,
        uploadAsync: false,
        showUpload: false,
        allowedFileExtensions: ['jpg', 'gif', 'jpeg', 'png']
    });


    $("#serieForm").validate({
        rules: {
            name: {
                required: true
            },
            channel: {
                required: true
            },
            releaseDate: {
                required: true,
                date: true
            }
        },
        errorPlacement: function(error, element) {
            if (element.attr("name") == "cover"){
                error.insertAfter($('.file-caption-main'));
            }else{
                error.insertAfter(element);
            }
        }
    });
});

function deleteActorDiv(element){
    $('input[value=' + element + ']').parent().remove();
}