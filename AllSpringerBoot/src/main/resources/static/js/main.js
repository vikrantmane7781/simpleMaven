const toggle=()=>{
	if($('.sidebar').is(":visible")){
		$('.sidebar').css("display","none");
		$('.content').css("margin-left","0%");
	}else{
		$('.sidebar').css("display","block");
		$('.content').css("margin-left","20%");
	}
};


const search=()=>{
	
	let query=$("#search").val();
	console.log(query);
	if(query==""){
		$(".search-reasult").hide();
	}else{
		console.log(query);
		
		//sending request
		
		let url=`http://localhost:8080/search/${query}`;
		fetch(url).then(response=>{
			return response.json();
		}).then((data)=>{
			console.log(data)
			let text=`<div class='list-group'>`;
			
			data.forEach((contact)=>{
				text+=`<a href='/user/contact/${contact.cid}' class='list-group-item list-group-item-action'>${contact.name}</a>`
			});
			text+=`</div>`;
			
			$(".search-reasult").html(text);
			$(".search-reasult").show();
		})
	}
	console.log("Search working....");
}