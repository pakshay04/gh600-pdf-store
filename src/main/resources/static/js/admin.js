const fileInput =
    document.getElementById('file');

const filename =
    document.getElementById('filename');

const upload =
    document.getElementById('upload');

const priceInput =
    document.getElementById('price');

const uploadResult =
    document.getElementById('uploadResult');

const link =
    document.getElementById('link');

const copy =
    document.getElementById('copy');

const sellerMessage =
    document.getElementById('sellerMsg');


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


    const price =
        Number(priceInput.value);


    if (!price || price < 1) {

        sellerMessage.textContent =
            'Please enter a valid price.';

        return;
    }


    const form =
        new FormData();


    form.append(
        'file',
        fileInput.files[0]
    );


    form.append(
        'pricePaise',
        Math.round(price * 100)
    );


    upload.disabled = true;


    try {

        const response =
            await fetch(
                '/api/pdfs/upload',
                {
                    method: 'POST',
                    body: form
                }
            );


        const data =
            await response.json();


        if (!response.ok) {

            throw new Error(
                data.error ||
                'Upload failed.'
            );
        }


        link.value =
            window.location.origin +
            data.purchaseUrl;


        uploadResult.classList.remove(
            'hidden'
        );


        sellerMessage.textContent =
            'PDF uploaded successfully.';


    } catch (error) {

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