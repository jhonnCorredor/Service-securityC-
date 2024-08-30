using Business.Interfaces.Operational;
using Data.Interfaces.Operational;
using Entity.Dto;
using Entity.Dto.Operational;
using Entity.Model.Operational;

namespace Business.Implements.Operational
{
    public class LotBusiness : ILotBusiness
    {
        private readonly ILotData data;

        public LotBusiness(ILotData data)
        {
            this.data = data;
        }

        public async Task Delete(int id)
        {
            await data.Delete(id);
        }

        public async Task<IEnumerable<LotDto>> GetAll()
        {
            IEnumerable<Lot> Lots = await data.GetAll();
            var lotDtos = Lots.Select(lot => new LotDto
            {
                Id = lot.Id,
                Num_hectareas = lot.Num_hectareas,
                CropId = lot.CropId,
                FarmId = lot.FarmId,
                State = lot.State
            });

            return lotDtos;
        }

        public async Task<IEnumerable<DataSelectDto>> GetAllSelect()
        {
            return await data.GetAllSelect();
        }

        public async Task<LotDto> GetById(int id)
        {
            Lot lot = await data.GetById(id);
            LotDto dto = new LotDto();
            dto.Id = lot.Id;
            dto.Num_hectareas = lot.Num_hectareas;
            dto.CropId = lot.CropId;
            dto.FarmId = lot.FarmId;
            dto.State = lot.State;
            return dto;
        }

        public Lot mapearDatos(Lot lot, LotDto entity)
        {
            lot.Id = entity.Id;
            lot.Num_hectareas = entity.Num_hectareas;
            lot.CropId = entity.CropId;
            lot.FarmId = entity.FarmId;
            lot.State = entity.State;
            return lot;
        }

        public async Task<Lot> Save(LotDto entity)
        {
            Lot lot = new Lot();
            lot = mapearDatos(lot, entity);
            lot.Created_at = DateTime.Now;
            lot.Updated_at = null;
            lot.Deleted_at = null;

            return await data.Save(lot);
        }

        public async Task Update(LotDto entity)
        {
            Lot farmCrop = await data.GetById(entity.Id);
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
