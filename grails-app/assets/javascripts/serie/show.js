$(document).ready(function() {
    $('#datatable-episodesTable').dataTable({
        "aaSorting": [[2, 'desc'], [3, 'desc']],
        "iDisplayLength": 5,
        "aLengthMenu": [5, 10, 25, 50, 100]
    });

    $('#datatable-episodesTable_filter > .col-sm-7').addClass('col-sm-5').removeClass('col-sm-6');
});