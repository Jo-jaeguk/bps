var posY;

// jQuery.curCSS = jQuery 플러그인 중 하위버전의 jQuery를 사용하는 경우 상위버전만 사용할 시 해당 오류 발생
jQuery.curCSS = function(element, prop, val) {
    return jQuery(element).css(prop, val);
};

$(function(){

	// 오버레이/버튼 클릭시 레이어 및 팝업&사이드바 처리
    $('#overlay').on('click', closeLayer);
    $('.btn-close_modal').on('click', closeLayer);
    
    // 버튼 토글(타겟 선택)
    $('.toggle-target').on('click', toggleTarget);
    
    // 버튼 토글(하나만 선택)
    $('.toggle-select').on('click', toggleSelect);

    // 버튼 토글(부모 선택)
    $('.toggle-parent').on('click', toggleParent);

    // 데이트피커
    // $(".datepicker").datepicker({
    //     showOn: "both", 
    //     buttonImage: "assets/img/date_range.png", 
    //     buttonImageOnly: true,
    //     dateFormat: 'yy-mm-dd',
    //     monthNames: ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'],
    //     dayNamesMin: ['일','월','화','수','목','금','토'],
    //     dayNames: ['일요일','월요일','화요일','수요일','목요일','금요일','토요일'] 
    // });
    
    // 테이블 자동화
    AutoTableSetting();
});
// 레이어 오픈
function openLayer(){
	$('#overlay').addClass('active');

    posY = $(window).scrollTop();
            
    $("html, body").addClass("not_scroll");
    // $(".section").css("top",-posY);
}

// 레이어 및 팝업&사이드바 클로즈
function closeLayer(e){
	if($('#navbar').hasClass('active')){
        $('#overlay').removeClass('active');
        $('#navbar').removeClass('active');

        $("html, body").removeClass("not_scroll");
        posY = $(window).scrollTop(posY);
    }
    
	if($('.main-modal').hasClass('active')){
        if($('.sub-modal').hasClass('active')){
            $('.main-modal').css('z-index', '105');
            $('.sub-modal').removeClass('active');
        }
        else{
            $('#overlay').removeClass('active');
            $('.main-modal').removeClass('active');

            $("html, body").removeClass("not_scroll");
            posY = $(window).scrollTop(posY);
        }
	}
}

// 버튼 토글(타겟 선택)
function toggleTarget(e){
	var target = $(e.currentTarget);

	$(target).toggleClass('active')
}

// 버튼 토글(하나만 선택)
function toggleSelect(e){
    var parent = $(e.currentTarget).parent();
	
	parent.children().removeClass('active');
    $(e.currentTarget).toggleClass('active');
    
    // 탭 메뉴인 경우 화면 전환
    if($(parent).attr('class') == 'tab-header'){
        var tab_container = parent.siblings('.tab-cont');
        var target_id = e.currentTarget.id;

        $(tab_container).find('.tab').removeClass('active');
        $(tab_container).find('.' + target_id).addClass('active');
    }
}

// 버튼 토글(타겟 선택)
function toggleParent(e){
	var target = $(e.currentTarget).parent();

	$(target).toggleClass('opened')
}

// 테이블 자동화
function AutoTableSetting(){
    var table_header = $('.table-body').find('.ti-header .t-item');
    var table_item = $('.table-body').find('.item-list .t-item');
    var item_count = $('.table-body').find('.ti-header .t-item').children().length;
    var set_width = [];

    for($i = 0; $i < item_count; $i++){
        set_width[$i] = $(table_header).children().eq($i).data('width');
    }

    for($i = 0; $i < $(table_item).length; $i++){
        for($j = 0; $j < item_count; $j++){
            $(table_item).eq($i).children().eq($j).css('width', set_width[$j] + '%');
        }
    }
}