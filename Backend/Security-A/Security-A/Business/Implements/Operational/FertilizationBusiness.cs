using Business.Interfaces.Operational;
using Data.Interfaces.Operational;
using Entity.Dto;
using Entity.Dto.Operational;
using Entity.Model.Operational;

namespace Business.Implements.Operational
{
    public class FertilizationBusiness : IFertilizationBusiness
    {
        private readonly IFertilizationData data;

        public FertilizationBusiness(IFertilizationData data)
        {
            this.data = data;
        }

        public async Task Delete(int id)
        {
            await data.Delete(id);
        }

        public async Task<IEnumerable<FertilizationDto>> GetAll()
        {
            IEnumerable<Fertilization> fertilizations = await data.GetAll();
            var fertilizationDtos = fertilizations.Select(fertilization => new FertilizationDto
            {
                Id = fertilization.Id,
                DateFertilization = fertilization.DateFertilization,
                QuantityMix = fertilization.QuantityMix,
                TypeFertilization = fertilization.TypeFertilization,
                State = fertilization.State
            });

            return fertilizationDtos;
        }

        public async Task<IEnumerable<DataSelectDto>> GetAllSelect()
        {
            return await data.GetAllSelect();
        }

        public async Task<FertilizationDto> GetById(int id)
        {
            Fertilization fertilization = await data.GetById(id);
            FertilizationDto dto = new FertilizationDto();
            dto.Id = fertilization.Id;
            dto.DateFertilization = fertilization.DateFertilization;
            dto.QuantityMix = fertilization.QuantityMix;
            dto.TypeFertilization = fertilization.TypeFertilization;
            dto.State = fertilization.State;
            return dto;
        }

        public Fertilization mapearDatos(Fertilization fertilization, FertilizationDto entity)
        {
            fertilization.Id = entity.Id;
            fertilization.DateFertilization = entity.DateFertilization;
            fertilization.QuantityMix = entity.QuantityMix;
            fertilization.TypeFertilization = entity.TypeFertilization;
            fertilization.State = entity.State;
            return fertilization;
        }

        public async Task<Fertilization> Save(FertilizationDto entity)
        {
            Fertilization fertilization = new Fertilization();
            fertilization = mapearDatos(fertilization, entity);
            fertilization.Created_at = DateTime.Now;
            fertilization.Updated_at = null;
            fertilization.Deleted_at = null;

            return await data.Save(fertilization);
        }

        public async Task Update(FertilizationDto entity)
        {
            Fertilization fertilization = await data.GetById(entity.Id);
            if (fertilization == null)
            {
                throw new Exception("Registro no encontrado");
            }
            fertilization = mapearDatos(fertilization, entity);
            fertilization.Updated_at = DateTime.Now;

            await data.Update(fertilization);
        }
    }
}
