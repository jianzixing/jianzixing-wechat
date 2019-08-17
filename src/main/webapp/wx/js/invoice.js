$(function () {
    $('#fpType li[head-type=0]').click(function () {
        $('#fpType li').removeClass('selected');
        $(this).addClass('selected');
        $('#taxLi').hide();
        $('#compName').hide();
        $('#fpTypeBar').html('个人');
    });
    $('#fpType li[head-type=1]').click(function () {
        $('#fpType li').removeClass('selected');
        $(this).addClass('selected');
        $('#taxLi').show();
        $('#compName').show();
        $('#fpTypeBar').html('公司');
    });

    $('#fpContent .type_addtips').click(function () {
        $('#fpContent .type_addtips').removeClass('selected');
        $(this).addClass('selected');
        $('#fpContent dt em').html($(this).attr('content_name'));
    });

    $('#fpContent .order_additional_tips_close').click(function () {
        $('#fpContent .order_additional_tips').hide();
    });

    $('#fp_selectNone').click(function () {
        $('#fp_general').hide();
        $('#invoinceTypes .type_addtips').removeClass('selected');
        $(this).addClass('selected');
        $('#fp_type_bar').html($($('#fp_selectNone').contents()[0]).text());
    });
    $('#fp_selectGeneral').click(function () {
        $('#fp_general').show();
        $('#invoinceTypes .type_addtips').removeClass('selected');
        $(this).addClass('selected');
        $('#fp_type_bar').html($($('#fp_selectGeneral').contents()[0]).text());
    });
});
