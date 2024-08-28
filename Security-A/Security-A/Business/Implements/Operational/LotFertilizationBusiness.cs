using Business.Interfaces.Security;
using Data.Interfaces.Security;
using Entity.Dto.Security;
using Entity.Dto;
using Entity.Model.Security;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Business.Interfaces.Operational;
using Data.Interfaces.Operational;
using Entity.Dto.Operational;

namespace Business.Implements.Operational
{
    public class LotFertilizationBusiness : ILotFertilizationBusiness
    {
        protected readonly ILotFertilizationData data;

        public LotFertilizationBusiness(ILotFertilizationData data)
        {
            this.data = data;
        }

        public async Task Delete(int id)
        {
            await data.Delete(id);
        }

        public async Task<IEnumerable<LotFertilizationDto>> GetAll()
        {
            IEnumerable<LotFertilization> LotFertilizations = await data.GetAll();
            var LotFertilizationDtos = LotFertilizations.Select(LotFertilization => new LotFertilizationDto
            {
                Id = LotFertilization.Id,
                LotId = LotFertilization.LotId,
                FertilizationId = LotFertilization.FertilizationId,
                State = LotFertilization.State
            });

            return LotFertilizationDtos;
        }

        public async Task<IEnumerable<DataSelectDto>> GetAllSelect()
        {
            return await data.GetAllSelect();
        }

        public async Task<LotFertilizationDto> GetById(int id)
        {
            LotFertilization LotFertilization = await data.GetById(id);
            LotFertilizationDto LotFertilizationDto = new LotFertilizationDto();

            LotFertilizationDto.Id = LotFertilization.Id;
            LotFertilizationDto.LotId = LotFertilization.LotId;
            LotFertilizationDto.FertilizationId = LotFertilization.FertilizationId;
            LotFertilizationDto.State = LotFertilization.State;
            return LotFertilizationDto;
        }

        public LotFertilization mapearDatos(LotFertilization LotFertilization, LotFertilizationDto entity)
        {
            LotFertilization.Id = entity.Id;
            LotFertilization.LotId = entity.LotId;
            LotFertilization.FertilizationId = entity.FertilizationId;
            LotFertilization.State = entity.State;
            return LotFertilization;
        }

        public async Task<LotFertilization> Save(LotFertilizationDto entity)
        {
            LotFertilization LotFertilization = new LotFertilization();
            LotFertilization = mapearDatos(LotFertilization, entity);
            LotFertilization.Created_at = DateTime.Now;
            LotFertilization.Deleted_at = null;
            LotFertilization.Updated_at = null;

            return await data.Save(LotFertilization);
        }

        public async Task Update(LotFertilizationDto entity)
        {
            LotFertilization LotFertilization = await data.GetById(entity.Id);
            if (LotFertilization == null)
            {
                throw new Exception("Registro no encontrado");
            }
            LotFertilization = mapearDatos(LotFertilization, entity);
            LotFertilization.Updated_at = DateTime.Now;

            await data.Update(LotFertilization);
        }
    }
}
