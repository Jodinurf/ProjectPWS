const modal = new bootstrap.Modal(document.getElementById("dataModal"));
const dataForm = document.getElementById("dataForm");
const dataBody = document.getElementById("dataBody");
const searchInput = document.getElementById("searchInput");
const btnOpenModal = document.getElementById("btnOpenModal");

let currentData = [];
let isEditMode = false;
let editNik = null;

// Format date in DD/MM/YYYY
function formatDate(dateString) {
  const date = new Date(dateString);
  const day = String(date.getDate()).padStart(2, "0");
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const year = date.getFullYear();
  return `${day}/${month}/${year}`;
}

// Open modal for Create Data
btnOpenModal.addEventListener("click", () => {
  isEditMode = false;
  modal.show();
  document.getElementById("modalTitle").innerText = "Create Data";
  dataForm.reset();

  document.getElementById("nikField").style.display = "block";
  document.getElementById("nikDisplay").style.display = "none"; 

  const nikInput = document.getElementById("nik");
  nikInput.removeAttribute("readonly");

  document.getElementById("photoPreview").style.display = "none";
});

function editDataModal(nik) {
  const data = currentData.find((d) => d.nik === nik);
  if (data) {
    isEditMode = true;
    editNik = nik;
    modal.show();
    document.getElementById("modalTitle").innerText = "Edit Data";

 
    document.getElementById("nikField").style.display = "none"; 
    document.getElementById("nikDisplay").style.display = "block"; 
    document.getElementById("nikDisplayText").innerText = data.nik; 
    const nikInput = document.getElementById("nik");
    nikInput.setAttribute("readonly", true); 

    document.getElementById("nama").value = data.nama || ''; 
    document.getElementById("tglLahir").value = data.tglLahir ? data.tglLahir.split("T")[0] : '';
    document.getElementById("alamat").value = data.alamat || ''; 

    const photoPreview = document.getElementById("photoPreview");
    if (data.photo) {
      photoPreview.style.display = "block";  
      photoPreview.src = `data:image/jpeg;base64,${data.photo}`;
    } else {
      photoPreview.style.display = "none";  
    }
  }
}


// Handle form submission
dataForm.addEventListener("submit", (event) => {
  event.preventDefault();
  const formData = new FormData(dataForm);
  
  if (isEditMode) {
    formData.delete("nik"); 
    const existingPhoto = currentData.find((d) => d.nik === editNik)?.photo;
    if (!formData.has("photo") && existingPhoto) {
      formData.append("photo", existingPhoto);
    }
  }

  const nama = formData.get("nama");
  const tglLahir = formData.get("tglLahir");
  const alamat = formData.get("alamat");

  // Validasi
  if (!nama || !tglLahir || !alamat) {
    Swal.fire({
      icon: "error",
      title: "Validation Error",
      text: "All fields are required.",
    });
    return;
  }

  const url = isEditMode ? `/dataKtp/update?nik=${editNik}` : "/dataKtp/create";
  const method = isEditMode ? "PUT" : "POST";

  fetch(url, {
    method,
    body: formData, 
  })
    .then((response) => response.json())
    .then(() => {
      Swal.fire({
        icon: "success",
        title: `Data ${isEditMode ? "updated" : "created"} successfully!`,
      });
      modal.hide();
      fetchData();
    })
    .catch((err) =>
      Swal.fire({
        icon: "error",
        title: "Error",
        text: err.message,
      })
    );
});

// Fetch data
function fetchData() {
  fetch("/dataKtp/getAllData")
    .then((response) => response.json())
    .then((data) => {
      currentData = data;
      updateTable(data);
    })
    .catch((err) =>
      Swal.fire({
        icon: "error",
        title: "Error Fetching Data",
        text: `An error occurred: ${err}`,
      })
    );
}

function updateTable(data) {
  dataBody.innerHTML = "";
  data.forEach((item) => {
    const row = document.createElement("tr");
    const photoUrl = item.photo
      ? `data:image/jpeg;base64,${item.photo}`
      : "/path/to/placeholder.jpg";

    row.innerHTML = `
      <td>${item.nik}</td>
      <td>${item.nama}</td>
      <td>${formatDate(item.tglLahir)}</td>
      <td>${item.alamat}</td>
      <td><img src="${photoUrl}" alt="Photo" width="50" height="50" class="thumbnail-photo" data-fullsize="${photoUrl}"></td>
      <td>
        <button class="btn btn-primary btn-sm" onclick="editDataModal('${item.nik}')">Edit</button>
        <button class="btn btn-danger btn-sm" onclick="deleteData('${item.nik}')">Delete</button>
      </td>
    `;
    dataBody.appendChild(row);
  });

  const thumbnailPhotos = document.querySelectorAll('.thumbnail-photo');
  thumbnailPhotos.forEach((photo) => {
    photo.addEventListener('click', (event) => {
      const fullSizePhotoUrl = event.target.getAttribute('data-fullsize');
      const modal = new bootstrap.Modal(document.getElementById('photoModal'));
      const fullSizePhoto = document.getElementById('fullSizePhoto');
      fullSizePhoto.src = fullSizePhotoUrl;
      modal.show();
    });
  });
}


// Delete data
function deleteData(nik) {
  Swal.fire({
    title: "Are you sure?",
    text: `Do you want to delete the data with NIK: ${nik}?`,
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "Yes, delete it!",
  }).then((result) => {
    if (result.isConfirmed) {
      fetch(`/dataKtp/delete?nik=${nik}`, { method: "DELETE" })
        .then(() => {
          Swal.fire("Deleted!", "The data has been deleted.", "success");
          fetchData();
        })
        .catch((err) =>
          Swal.fire({
            icon: "error",
            title: "Error",
            text: err.message,
          })
        );
    }
  });
}

// Search functionality
searchInput.addEventListener("input", (event) => {
  const searchTerm = event.target.value.toLowerCase();
  const filteredData = currentData.filter(
    (item) =>
      item.nik.toLowerCase().includes(searchTerm) ||
      item.nama.toLowerCase().includes(searchTerm)
  );
  updateTable(filteredData);
});

// Initial fetch
fetchData();
