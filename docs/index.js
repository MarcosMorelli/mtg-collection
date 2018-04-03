function textFilter() {
	let input = document.getElementById("text_filter");
	let filter = input.value.toUpperCase();
	let cards = document.getElementsByName("card");
	for (let card of cards) {	
		if (card.innerHTML.toUpperCase().indexOf(filter) > -1) {
			card.parentNode.parentNode.parentNode.style.display = 'block';
		} else {
			card.parentNode.parentNode.parentNode.style.display = 'none';
		}
	}
}