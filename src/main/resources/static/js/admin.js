// =====================================================
// ADMIN APP
// =====================================================

const fileInput = document.getElementById('file');
const filename = document.getElementById('filename');
const upload = document.getElementById('upload');
const priceInput = document.getElementById('price');
const uploadResult = document.getElementById('uploadResult');
const link = document.getElementById('link');
const copy = document.getElementById('copy');
const sellerMessage = document.getElementById('sellerMsg');


// =====================================================
// GET CSRF TOKEN
// =====================================================

async function getCsrfToken() {

    const response = await fetch('/admin/csrf', {
        method: 'GET',
        credentials: 'same-origin'
    });

    if (!response.ok) {
        throw new Error('Unable to obtain CSRF token.');
    }

    const data = await response.json();

    if (!data.token) {
        throw new Error('CSRF token was not returned by server.');
    }

    return data.token;
}


// =====================================================
// FILE SELECTION
// =====================================================

fileInput.addEventListener('change', () => {

    filename.textContent =
        fileInput.files.length
            ? fileInput.files[0].name
            : '';

});


// =====================================================
// UPLOAD
// =====================================================

upload.addEventListener('click', async () => {

    sellerMessage.textContent = '';

    if (!fileInput.files.length) {

        sellerMessage.textContent =
            'Please select a PDF.';

        return;
    }


    const price = Number(priceInput.value);

    if (!price || price < 1) {

        sellerMessage.textContent =
            'Please enter a valid price.';

        return;
    }


    upload.disabled = true;


    try {

        // ---------------------------------------------
        // Get CSRF token
        // ---------------------------------------------

        const csrfToken =
            await getCsrfToken();


        // ---------------------------------------------
        // Prepare multipart form
        // ---------------------------------------------

        const form = new FormData();

        form.append(
            'file',
            fileInput.files[0]
        );

        form.append(
            'pricePaise',
            Math.round(price * 100)
        );


        // ---------------------------------------------
        // Upload PDF
        // ---------------------------------------------

        const response = await fetch(
            '/api/pdfs/upload',
            {
                method: 'POST',

                credentials: 'same-origin',

                headers: {
                    'X-XSRF-TOKEN': csrfToken
                },

                body: form
            }
        );


        // ---------------------------------------------
        // Read response safely
        // ---------------------------------------------

        const text =
            await response.text();

        let data = {};

        if (text) {

            try {
                data = JSON.parse(text);
            } catch (e) {
                throw new Error(
                    'Server returned an invalid response.'
                );
            }
        }


        if (!response.ok) {

            throw new Error(
                data.error ||
                data.message ||
                'Upload failed.'
            );
        }


        // ---------------------------------------------
        // Purchase link
        // ---------------------------------------------

        link.value =
            window.location.origin +
            data.purchaseUrl;


        uploadResult.classList.remove(
            'hidden'
        );


        sellerMessage.textContent =
            'PDF uploaded successfully.';


    } catch (error) {

        console.error(
            'Admin upload error:',
            error
        );

        sellerMessage.textContent =
            error.message;

    } finally {

        upload.disabled = false;

    }

});


// =====================================================
// COPY PURCHASE LINK
// =====================================================

copy.addEventListener('click', async () => {

    try {

        await navigator.clipboard.writeText(
            link.value
        );

        copy.textContent =
            'Copied!';


        setTimeout(() => {

            copy.textContent =
                'Copy Link';

        }, 1500);


    } catch (error) {

        sellerMessage.textContent =
            'Could not copy the link.';

    }

});