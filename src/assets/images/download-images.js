// This is a utility script to download images
// You can run this with Node.js to download the images

const https = require('https');
const fs = require('fs');
const path = require('path');

// List of images to download
const images = [
  {
    name: 'soccer-field-night.jpg',
    url: 'https://img.freepik.com/free-photo/soccer-field-with-green-grass-night_587448-4105.jpg'
  },
  {
    name: 'rocket-icon.png',
    url: 'https://cdn-icons-png.flaticon.com/512/3069/3069172.png'
  },
  {
    name: 'tournament-image.jpg',
    url: 'https://img.freepik.com/free-photo/sports-tools_53876-138077.jpg'
  },
  {
    name: 'equipment-image.jpg',
    url: 'https://img.freepik.com/free-photo/sports-tools_53876-138077.jpg'
  },
  {
    name: 'formation-image.jpg',
    url: 'https://img.freepik.com/free-photo/coach-explaining-game-strategy-his-team_23-2149758138.jpg'
  }
];

// Function to download an image
function downloadImage(url, filename) {
  return new Promise((resolve, reject) => {
    const file = fs.createWriteStream(filename);
    
    https.get(url, response => {
      response.pipe(file);
      
      file.on('finish', () => {
        file.close();
        console.log(`Downloaded ${filename}`);
        resolve();
      });
      
      file.on('error', err => {
        fs.unlink(filename);
        console.error(`Error downloading ${filename}:`, err.message);
        reject(err);
      });
    }).on('error', err => {
      fs.unlink(filename);
      console.error(`Error downloading ${filename}:`, err.message);
      reject(err);
    });
  });
}

// Download all images
async function downloadAllImages() {
  for (const image of images) {
    const filePath = path.join(__dirname, image.name);
    try {
      await downloadImage(image.url, filePath);
    } catch (error) {
      console.error(`Failed to download ${image.name}`);
    }
  }
}

downloadAllImages().then(() => {
  console.log('All downloads completed');
}).catch(error => {
  console.error('Error downloading images:', error);
});
