$(document).ready(function() {
    $("#video").fileinput({
        maxFileCount: 1,
        uploadAsync: false,
        showUpload: false,
        allowedFileExtensions: ['3g2', '3gp', 'aaf', 'asf', 'avchd', 'avi', 'drc', 'flv', 'm2v', 'm4p', 'm4v', 'mkv', 'mng', 'mov', 'mp2', 'mp4', 'mpe', 'mpeg', 'mpg', 'mpv', 'mxf', 'nsv', 'ogg', 'ogv', 'qt', 'rm', 'rmvb', 'roq', 'svi', 'vob', 'webm', 'wmv', 'yuv']
    });

    $.validator.addMethod('positiveNumber',
        function (value) {
            return Number(value) > 0;
        }, 'Enter a positive number.');

    $.validator.addMethod('episodeVideo',
        function (value) {
            console.log(value);
            return document.getElementsByTagName("video")[0] != null || (value != null && value != "") ;
        }, 'Attach a video.');

    $("#episodeForm").validate({
        rules: {
            title: {
                required: true
            },
            season: {
                required: true,
                positiveNumber: true
            },
            episodeNumber: {
                required: true,
                positiveNumber: true
            },
            summary: {
                maxlength: 4000
            },
            video: {
                episodeVideo: true
            }
        },
        errorPlacement: function(error, element) {
            if (element.attr("name") == "video"){
                error.insertAfter($('.file-caption-main'));
            }else{
                error.insertAfter(element);
            }
        }
    });
});
