using Business.Interfaces.Parameter;
using Data.Interfaces.Operational;
using Entity.Dto;
using Entity.Dto.Parameter;
using Entity.Model.Parameter;

namespace Business.Implements.Operational
{
    public class FarmCropBusiness : IFarmCropBusiness
    {
        private readonly IFarmCropData data;

        public FarmCropBusiness(IFarmCropData data)
        {
            this.data = data;
        }

        public async Task Delete(int id)
        {
            await data.Delete(id);
        }

        public async Task<IEnumerable<FarmCropDto>> GetAll()
        {
            IEnumerable<FarmCrop> farmCrops = await data.GetAll();
            var FarmCropDtos = farmCrops.Select(FarmCrop => new FarmCropDto
            {
                Id = FarmCrop.Id,
                Num_hectareas = FarmCrop.Num_hectareas,
                CropId = FarmCrop.CropId,
                FarmId = FarmCrop.FarmId,
                State = FarmCrop.State
            });

            return FarmCropDtos;
        }

        public async Task<IEnumerable<DataSelectDto>> GetAllSelect()
        {
            return await data.GetAllSelect();
        }

        public async Task<FarmCropDto> GetById(int id)
        {
            FarmCrop farmCrop = await data.GetById(id);
            FarmCropDto dto = new FarmCropDto();
            dto.Id = farmCrop.Id;
            dto.Num_hectareas = farmCrop.Num_hectareas;
            dto.CropId = farmCrop.CropId;
            dto.FarmId = farmCrop.FarmId;
            dto.State = farmCrop.State;
            return dto;
        }

        public FarmCrop mapearDatos(FarmCrop farmCrop, FarmCropDto entity)
        {
            farmCrop.Id = entity.Id;
            farmCrop.Num_hectareas = entity.Num_hectareas;
            farmCrop.CropId = entity.CropId;
            farmCrop.FarmId = entity.FarmId;
            farmCrop.State = entity.State;
            return farmCrop;
        }

        public async Task<FarmCrop> Save(FarmCropDto entity)
        {
            FarmCrop farmCrop = new FarmCrop();
            farmCrop = mapearDatos(farmCrop, entity);
            farmCrop.Created_at = DateTime.Now;
            farmCrop.Updated_at = null;
            farmCrop.Deleted_at = null;

            return await data.Save(farmCrop);
        }

        public async Task Update(FarmCropDto entity)
        {
            FarmCrop farmCrop = await data.GetById(entity.Id);
            if (farmCrop == null)
            {
                throw new Exception("Registro no encontrado");
            }
            farmCrop = mapearDatos(farmCrop, entity);
            farmCrop.Updated_at = DateTime.Now;

            await data.Update(farmCrop);
        }
    }
}
