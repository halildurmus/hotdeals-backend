db.createCollection('categories', {capped: false})
db.createCollection('stores', {capped: false})

db.categories.insertMany([
  {
    names: {en: 'Computers', tr: 'Bilgisayar'},
    parent: '/',
    category: '/computers',
    iconLigature: 'computer',
    iconFontFamily: 'MaterialIcons',
    _class: 'category'
  },
  {
    names: {en: 'Electronics', tr: 'Elektronik '},
    parent: '/',
    category: '/electronics',
    iconLigature: 'devices',
    iconFontFamily: 'MaterialIcons',
    _class: 'category'
  },
  {
    names: {en: 'Video Cards', tr: 'Ekran Kartı'},
    parent: '/computers',
    category: '/computers/video-cards',
    iconLigature: 'memory',
    iconFontFamily: 'MaterialIcons',
    _class: 'category'
  },
])

db.stores.insertMany([
  {
    name: 'Amazon',
    logo: 'https://logoeps.com/wp-content/uploads/2011/05/amazon-logo-vector.png',
    _class: 'store'
  },
  {
    name: 'BestBuy',
    logo: 'https://upload.wikimedia.org/wikipedia/commons/thumb/f/f5/Best_Buy_Logo.svg/1280px-Best_Buy_Logo.svg.png',
    _class: 'store'
  },
  {
    name: 'Walmart',
    logo: 'https://upload.wikimedia.org/wikipedia/commons/thumb/c/ca/Walmart_logo.svg/800px-Walmart_logo.svg.png',
    _class: 'store'
  },
])